package steps.credentials

import steps.Steps
import utils.MailUtils


class RecoverPasswordSteps extends Steps with MailUtils {

  Given("""^I ask to recover the password for email (.*)$"""){ (email : String) =>

    stepsData.response = http("/rest/auth/initRecovery").postData(json("email" -> email)).asString
  }

}


