package errors

import java.util.UUID

import play.api.data.validation.ValidationError
import play.api.libs.json.{JsPath, Json, Writes}
import play.api.libs.functional.syntax._
import utils.JsonUtils._
/**
  * Created by francois on 16/03/17.
  */
case class ErrorsForJsPath(jsPath: JsPath, errors:Seq[ValidationError]){
  def this(t : (JsPath, Seq[ValidationError])) = this(t._1,t._2)
}

case class AuthenticationException(code : String,
                                   description : String,
                                   cause : Option[Throwable] = None,
                                   inputExceptions : Seq[ErrorsForJsPath] = Nil,
                                   uuid : Option[UUID] = None
                                  )
  extends RuntimeException(code  + " " + description, cause.getOrElse(null)) {
}

object AuthenticationException{
  def ts(jsPath: JsPath) = jsPath.toString()
  implicit val jsPathWrites : Writes[JsPath] = implicitly[Writes[String]].contramap[JsPath](ts)
  implicit val validationErrorWrites : Writes[ValidationError] = implicitly[Writes[String]].contramap[ValidationError](_.message)
  implicit val errorsForJsPath = Json.writes[ErrorsForJsPath]

  implicit val writes: Writes[AuthenticationException] = (
    (JsPath \ "code").write[String] and
      (JsPath \ "description").write[String] and
      (JsPath \ "inputExceptions").writeNullable[Seq[ErrorsForJsPath]] and
      (JsPath \ "uuid").writeNullable[String]
    ) ((e :AuthenticationException) =>
      (e.code,
      e.description,
      Option(e.inputExceptions).filterNot(_.isEmpty),
      e.uuid.map(_.toString)
    ))
}
