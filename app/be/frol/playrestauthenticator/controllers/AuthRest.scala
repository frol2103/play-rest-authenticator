package be.frol.playrestauthenticator.controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasher}
import com.mohiva.play.silhouette.api.{Environment, LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers._
import be.frol.playrestauthenticator.errors.{AuthenticationException, InvalidCredentialsException}
import be.frol.playrestauthenticator.models.{Profile, User, UserToken}
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Json, Reads}
import play.api.mvc._
import be.frol.playrestauthenticator.services.{UserService, UserTokenService}
import be.frol.playrestauthenticator.utils.FutureUtils._
import be.frol.playrestauthenticator.utils.Mailer
import be.frol.playrestauthenticator.utils.Utils._
import be.frol.playrestauthenticator.utils.bodyparser.JsonParser

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
    loginInfo(email), false, email.toOpt,
    firstname.toOpt, lastname.toOpt, None,
    passwordHasher.hash(password).toOpt,
    None)
  )

  implicit val credentialsFormat = Json.format[Credentials]

  def loginInfo(email: String) = LoginInfo(CredentialsProvider.ID, email)

  def signUp = Action.async(JsonParser.successWith(newUserCredentialsRead)) { implicit request =>
      request.body
      .zipMap(userService.saveNewUser)
      .flatMap{case (p,u) => authInfoRepository.save(p.loginInfo, p.passwordInfo.get).map(_ => (p,u))}
      .flatMap{case (p,u) => userTokenService.save(UserToken.create(u.id, p.email.get, true)).map(t => (p,u,t))}
      .flatMap{case (p,u,t) => mailer.welcome(p, link = routes.AuthRest.confirm(t.id.toString).absoluteURL())}
      .map(_ => Ok)
  }

  def signIn = Action.async(JsonParser.success[Credentials]) { implicit request =>
      request.body
      .flatMap(credentialsProvider.authenticate)
      .zipMap(userService.retrieve)
      .map {
        case (_,None) => throw new InvalidCredentialsException("Invalid credentials")
        case (li,Some(user)) if !user.profileFor(li).map(_.confirmed).getOrElse(false) => throw new AuthenticationException("USER_NOT_CONFIRMED","The user is not confirmed")
        case (li,_) => li
      }
      .flatMap(env.authenticatorService.create(_))
      .flatMap(env.authenticatorService.init)
      .flatMap(env.authenticatorService.embed(_, Ok))
  }


  def confirm(tokenId: String) = Action.async { implicit request =>
    Future.successful(UUID.fromString(tokenId))
      .flatMap(userTokenService.find)
      .flatMap(topt => topt.map(t => userService.find(t.userId).map(_.map(t -> _))).getOrElse(Future.successful(None)))
      .map {
        case None => throw new AuthenticationException("TOKEN_ERROR", "wrong token")
        case Some((token, u)) => {
          userTokenService.remove(token.id)
          if (token.isSignUp && !token.isExpired) {
            userService.confirm(loginInfo(token.email))
            Ok
          } else {
            throw new AuthenticationException("EXPIRED_TOKEN", "expired token")
          }
        }
      }
  }
}
