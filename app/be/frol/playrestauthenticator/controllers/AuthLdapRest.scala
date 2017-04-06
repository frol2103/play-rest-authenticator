package be.frol.playrestauthenticator.controllers

import java.util
import javax.inject.Inject

import be.frol.playrestauthenticator.models.User
import be.frol.playrestauthenticator.providers.LdapProvider
import be.frol.playrestauthenticator.services.{UserService, UserTokenService}
import be.frol.playrestauthenticator.utils.Mailer
import be.frol.playrestauthenticator.utils.bodyparser.JsonParser
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasher}
import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers._
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc._

import scala.language.implicitConversions

class AuthLdapRest @Inject()(
                              val messagesApi: MessagesApi,
                              val env: Environment[User, CookieAuthenticator],
                              ldapProvider: LdapProvider,
                              authInfoRepository: AuthInfoRepository,
                              credentialsProvider: CredentialsProvider,
                              userService: UserService,
                              userTokenService: UserTokenService,
                              avatarService: AvatarService,
                              passwordHasher: PasswordHasher,
                              configuration: Configuration,
                              mailer: Mailer) extends Silhouette[User, CookieAuthenticator] with SignInTrait {


  implicit val credentialsFormat = Json.format[Credentials]

  def toHashtable(env: Map[String, String]) = {
    val envTable = new util.Hashtable[String, String]()
    env.foreach { case (k, v) => envTable.put(k, v) }
    envTable
  }

  def signIn = Action.async(JsonParser.success[Credentials]) { implicit request =>
    signInLoginInfo {
      request.body
        .flatMap { credentials =>
          ldapProvider.authenticate(credentials)
        }
    }
  }


}
