package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasher}
import com.mohiva.play.silhouette.api.{Environment, LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers._
import models.{Profile, User, UserToken}
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Json, Reads}
import play.api.mvc._
import services.{UserService, UserTokenService}
import utils.Mailer
import utils.Utils._
import utils.FutureUtils._

import scala.concurrent.Future
import scala.language.implicitConversions

class AuthRest @Inject()(
                          val messagesApi: MessagesApi,
                          val env: Environment[User, CookieAuthenticator],
                          authInfoRepository: AuthInfoRepository,
                          credentialsProvider: CredentialsProvider,
                          userService: UserService,
                          userTokenService: UserTokenService,
                          avatarService: AvatarService,
                          passwordHasher: PasswordHasher,
                          configuration: Configuration,
                          mailer: Mailer) extends Silhouette[User,CookieAuthenticator] {


  implicit val newUserCredentialsRead: Reads[Profile] = (
    (JsPath \ "firstname").read[String](minLength[String](2)) and
      (JsPath \ "lastname").read[String](minLength[String](2)) and
      (JsPath \ "email").read[String](minLength[String](2)) and
      (JsPath \ "password").read[String](minLength[String](2))
    ) ((firstname, lastname, email, password) => new Profile(
    loginInfo(email), true, email.toOpt,
    firstname.toOpt, lastname.toOpt, None,
    passwordHasher.hash(password).toOpt,
    None)
  )

  implicit val credentialsFormat = Json.format[Credentials]

  def loginInfo(email: String) = LoginInfo(CredentialsProvider.ID, email)

  def signUp = Action.async(BodyParsers.parse.json) { request =>
    Future {
      request.body.validate[Profile](newUserCredentialsRead).get
    }
      .flatMap(userService.saveNewUser)
      .flatMap(u => authInfoRepository.save(u.profiles.head.loginInfo, u.profiles.head.passwordInfo.get).map(_ => u))
      .flatMap(u => userTokenService.save(UserToken.create(u.id, u.profiles.head.email.get, true)).map(_ => u))
      .map(_ => Ok("Success"))
  }


  def signIn = Action.async(BodyParsers.parse.json) { implicit request =>
    Future {
      request.body.validate[Credentials].get
    }
      .flatMap(credentialsProvider.authenticate)
      .zipMap(userService.retrieve)
      .map {
        case (_,None) => throw new RuntimeException("Incorrect Credentials")
        case (li,Some(user)) if !user.profileFor(li).map(_.confirmed).getOrElse(false) => throw new RuntimeException("not confirmed")
        case (li,_) => li
      }
      .flatMap(env.authenticatorService.create(_))
      .flatMap(env.authenticatorService.init)
      .flatMap(env.authenticatorService.embed(_, Ok("success")))
  }

  def profile = UserAwareAction.async{ request =>
    Future.successful(Ok(request.identity.toString))
  }


}
