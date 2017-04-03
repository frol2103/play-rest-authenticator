package controllers

import java.util
import javax.inject.Inject
import javax.naming.Context
import javax.naming.directory.{InitialDirContext, SearchControls}

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasher}
import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers._
import models.User
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc._
import providers.LdapProvider
import services.{UserService, UserTokenService}
import utils.Mailer
import utils.bodyparser.JsonParser

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
                              mailer: Mailer) extends Silhouette[User, CookieAuthenticator] {


  implicit val credentialsFormat = Json.format[Credentials]

  def toHashtable(env: Map[String, String]) = {
    val envTable = new util.Hashtable[String, String]()
    env.foreach { case (k, v) => envTable.put(k, v) }
    envTable
  }

  def signIn = Action.async(JsonParser.success[Credentials]) { implicit request =>
    request.body
      .map { credentials =>
        ldapProvider.authenticate(credentials)
      }
      .map(v => Ok(v.toString))
  }


}
