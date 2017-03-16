package errors

import java.util.UUID

import play.api.libs.json.{JsPath, Json, Writes}
import play.api.libs.functional.syntax._

/**
  * Created by francois on 16/03/17.
  */
case class AuthenticationException(code : String,
                                   description : String,
                                   cause : Option[Throwable] = None,
                                   uuid : Option[UUID] = None
                                  )
  extends RuntimeException(code  + " " + description, cause.getOrElse(null)) {
}

object AuthenticationException{
  implicit val writes: Writes[AuthenticationException] = (
    (JsPath \ "code").write[String] and
      (JsPath \ "description").write[String] and
      (JsPath \ "uuid").writeNullable[String]
    ) ((e :AuthenticationException) =>
      (e.code,
      e.description,
      e.uuid.map(_.toString)
    ))
}
