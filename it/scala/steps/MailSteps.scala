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

  When("""^I visit the link in the mail$"""){ () =>
    val link = """href="(.*)"""".r.findFirstMatchIn(stepsData.email.body)
    link.isDefined should be(true)
    stepsData.response = Http(link.get.group(1)).asString
  }



}


