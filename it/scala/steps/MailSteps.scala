package steps

import play.api.libs.json.Json
import steps.support.mailhog.Mail
import utils.JsonUtils._

import scalaj.http.Http

class MailSteps extends Steps {


  def getAllMails() = {
    val response = Http("http://smtp:8025/api/v1/messages")
      .header("Content-Type", "application/json;")
      .timeout(connTimeoutMs = 1000, readTimeoutMs = 10000)
      .asString

    Json.parse(response.body)
      .validate[List[Mail]]
      .get

  }

  Then("""^a mail should have been sent to (.*)$""") { (email: String) =>
    val sentMail = getAllMails().find(_.to.contains(email))
    sentMail.isDefined should be(true)
    stepsData.email = sentMail.get
  }

  When("""^I visit the link in the mail$""") { () =>
    stepsData.response = Http(linkInMail).asString
  }

  When("""^I visit the link in the mail with altered last char$""") {
    val link = linkInMail
    val alteredLink = link.dropRight(1) + (if (link.takeRight(1) == "a") "b" else "a")
    stepsData.response = Http(alteredLink).asString
  }


  private def linkInMail = {
    """href="(.*)"""".r.findFirstMatchIn(stepsData.email.body)
      .getOrElse(throw new RuntimeException("No link found in the mail"))
      .group(1)
  }

}


