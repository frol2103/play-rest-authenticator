package utils

import play.api.libs.json.Json
import steps.support.mailhog.Mail

import scalaj.http.Http

/**
  * Created by francois on 23/03/17.
  */
trait MailUtils {

  protected def getAllMails() = {
    val response = Http("http://smtp:8025/api/v1/messages")
      .header("Content-Type", "application/json;")
      .timeout(connTimeoutMs = 1000, readTimeoutMs = 10000)
      .asString

    Json.parse(response.body)
      .validate[List[Mail]]
      .get

  }

  protected def mailFor(email:String) = {
    getAllMails().find(_.to.contains(email))
  }

  protected def linkInMail(email : Mail) = {
    """href="(.*)"""".r.findFirstMatchIn(email.body)
      .getOrElse(throw new RuntimeException("No link found in the mail"))
      .group(1)
  }
}
