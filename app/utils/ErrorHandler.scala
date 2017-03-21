package utils

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.SecuredErrorHandler
import com.mohiva.play.silhouette.impl.exceptions.{IdentityNotFoundException, InvalidPasswordException}
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import play.api.routing.Router
import play.api.{Configuration, OptionalSourceMapper}

import scala.concurrent.Future
import controllers.routes
import errors.AuthenticationException
import errors.AuthenticationException._
import org.slf4j.LoggerFactory
import play.api.libs.json.Json

class ErrorHandler @Inject() (
  val messagesApi: MessagesApi,
  env: play.api.Environment,
  config: Configuration,
  sourceMapper: OptionalSourceMapper,
  router: javax.inject.Provider[Router])
  extends DefaultHttpErrorHandler(env, config, sourceMapper, router)
  with SecuredErrorHandler with I18nSupport {

  override def onNotAuthenticated(request: RequestHeader, messages: Messages): Option[Future[Result]] =
    Some(Future.successful(Redirect(routes.Auth.signIn())))

  override def onNotAuthorized(request: RequestHeader, messages: Messages): Option[Future[Result]] =
    Some(Future.successful(Redirect(routes.Auth.signIn()).flashing("errors" -> Messages("error.accessDenied")(messages))))

  override def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    Future.successful(NotFound(views.html.errors.notFound(request)))
  }

  override def onServerError(request:RequestHeader, exception:Throwable):Future[Result] = {
    val uuid = UUID.randomUUID()
    ErrorHandler.logger.error("Error " + uuid  + " : " + exception.getMessage, exception )
    Future.successful(exception match {
      case e : AuthenticationException => BadRequest(toJsonWithUUID(uuid, e))
      case e @(_: IdentityNotFoundException | _:InvalidPasswordException) => BadRequest(toJsonWithUUID(uuid,
        new AuthenticationException("INVALID_CREDENTIALS", "Invalid credentials", Some(e))))
      case e => {
        val authEx = new AuthenticationException("TECHNICAL", "Technical error", Some(e), uuid = Some(uuid))
        InternalServerError(Json.toJson(authEx))
      }
    })
  }

  private def toJsonWithUUID(uuid: UUID, e: AuthenticationException) = {
    Json.toJson(e.copy(uuid = Some(uuid)))
  }
}

object ErrorHandler{
  val logger = LoggerFactory.getLogger(this.getClass)
}
