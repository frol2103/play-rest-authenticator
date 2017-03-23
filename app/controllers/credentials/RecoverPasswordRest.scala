package controllers.credentials

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import errors.AuthenticationException
import models.{User, UserToken}
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.JsPath
import play.api.libs.json.Reads._
import play.api.mvc._
import services.{UserService, UserTokenService}
import utils.FutureUtils._
import utils.Mailer
import utils.bodyparser.JsonParser

import scala.concurrent.Future
import scala.language.implicitConversions

class RecoverPasswordRest @Inject()(
                                     val env: Environment[User, CookieAuthenticator],
                                     val messagesApi: MessagesApi,
                                     userService: UserService,
                                     userTokenService: UserTokenService,
                                     mailer: Mailer) extends Silhouette[User, CookieAuthenticator] with CredentialsAction {

  val emailReads = (JsPath \ "email").read[String]

  def initToken = Action.async(JsonParser.successWith(emailReads)) { implicit request =>
    request.body
      .zipMap(email => userService.retrieve(loginInfo(email)))
      .flatMap {
        case (_, None) => throw new AuthenticationException("UNKNOWN_EMAIL", "unknown email")
        case (email, Some(user)) => userTokenService.save(UserToken.create(user.id, email, isSignUp = false))
          .map(email -> _)
      }
      .flatMap { case (email, token) => mailer.resetPassword(email, link = routes.RecoverPasswordRest.resetPassword(token.id.toString).absoluteURL()) }
      .map(_ => Ok)
  }

  def resetPassword(token: String) = Action.async(
    Future.failed(new RuntimeException("not implemented"))
  )

}
