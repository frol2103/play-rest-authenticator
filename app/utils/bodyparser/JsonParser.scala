package utils.bodyparser

import errors.AuthenticationException
import org.bouncycastle.asn1.cms.AuthEnvelopedData
import play.api.libs.json.Reads
import play.api.mvc.{BodyParser, BodyParsers}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by francois on 16/03/17.
  */
object JsonParser {
  def apply[T](implicit reads:Reads[T], executionContext: ExecutionContext) : BodyParser[T] = {
    BodyParsers.parse.json
      .map(_.validate[T].getOrElse(throw new AuthenticationException("INCORRECT_DATA", "Incorrect input data")))
  }

  def success[T](implicit reads:Reads[T], executionContext: ExecutionContext) = apply.map(Future.successful(_))
  def successWith[T](reads:Reads[T])(implicit executionContext: ExecutionContext) = apply(reads,executionContext).map(Future.successful(_))
}
