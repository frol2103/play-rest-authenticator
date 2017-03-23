package steps

import play.api.libs.json.Json
import steps.support.mailhog.Mail
import utils.JsonUtils._
import utils.MailUtils

import scalaj.http.Http


class MailSteps extends Steps with MailUtils {

  Then("""^a mail should have been sent to (.*)$""") { (email: String) =>
    val sentMail = mailFor(email)
    sentMail.isDefined should be(true)
    stepsData.email = sentMail.get
  }

  When("""^I visit the link in the mail$""") { () =>
    stepsData.response = Http(linkInMail(stepsData.email)).asString
  }

  When("""^I visit the link in the mail with altered last char$""") {
    val link = linkInMail(stepsData.email)
    val alteredLink = link.dropRight(1) + (if (link.takeRight(1) == "a") "b" else "a")
    stepsData.response = Http(alteredLink).asString
  }


}


