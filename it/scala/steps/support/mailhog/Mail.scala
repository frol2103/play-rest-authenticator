package steps.support.mailhog

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

/**
  * Created by francois on 21/03/17.
  */
case class Mail(to: List[String], body: String)

object Mail {

  val addressReads = (
    (JsPath \ "Mailbox").read[String] and
      (JsPath \ "Domain").read[String]
    ) ((m, d) => m + "@" + d)

  implicit val reads: Reads[Mail] = (
    (JsPath \ "To").read[List[String]](Reads.list(addressReads)) and
      (JsPath \ "Content" \ "Body").read[String]
    ) ((to,body) => Mail(to,body))

}
