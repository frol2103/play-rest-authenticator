package be.frol.playrestauthenticator.utils.bodyparser

import be.frol.playrestauthenticator.errors.{AuthenticationException, ErrorsForJsPath}
import org.bouncycastle.asn1.cms.AuthEnvelopedData
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsSuccess, Reads}
import play.api.mvc.{BodyParser, BodyParsers}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

/**
  * Created by francois on 16/03/17.
  */
object JsonParser {
  def apply[T](implicit reads:Reads[T], executionContext: ExecutionContext) : BodyParser[T] = {
    BodyParsers.parse.json
      .map(_.validate[T])
      .map {
        case JsSuccess(s,_) => s
        case JsError(e) => throw new AuthenticationException("INCORRECT_DATA", "Incorrect input data", inputExceptions =  e.map(new ErrorsForJsPath(_)))
      }
  }

  def success[T](implicit reads:Reads[T], executionContext: ExecutionContext) = apply.map(Future.successful(_))
  def successWith[T](reads:Reads[T])(implicit executionContext: ExecutionContext) = apply(reads,executionContext).map(Future.successful(_))
}
