package steps

import utils.MailUtils

import scalaj.http.Http


class RecoverPasswordSteps extends Steps with MailUtils {

  Given("""^I ask to recover the password for email (.*)$"""){ (email : String) =>

    stepsData.response = http("/rest/auth/initRecovery").postData(json("email" -> email)).asString
  }

}


