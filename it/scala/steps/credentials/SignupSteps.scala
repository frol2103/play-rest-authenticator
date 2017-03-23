package steps.credentials

import play.api.libs.json.{JsPath, JsString}
import steps.Steps
import utils.JsonUtils._
import utils.MailUtils

import scalaj.http.Http

class SignupSteps extends Steps with MailUtils{


  Given("""I signup with email (.*) and password (.*)$"""){(email:String, password:String) =>
    signup(email, password)
  }

  private def signup(email: String, password: String = "testPassword", firstname: String = "foo", lastname: String = "bar") = {
    stepsData.response = http("/rest/auth/signup").put(
      json("firstname" -> firstname,
        "lastname" -> lastname,
        "email" -> parse(email),
        "password" -> parse(password))
    ).asString
  }

  Given("""^I am a registered user with email (.*)$"""){(email :String) =>
    signup(email)
    Http(linkInMail(mailFor(email).get)).asString.code should be(200)
  }

  Given("""I signup with email (.*) password (.*) firstname (.*) and lastname (.*)""") {
    (email: String, password: String, firstname: String, lastname: String) => signup(email, password, firstname, lastname)
  }

  Given("""I signin with email (.*) and password (.*)$"""){(email:String, password:String) =>
    stepsData.response = http("/rest/auth/signin").postData(
      json("identifier"->parse(email),
        "password"->parse(password))
    ).asString
  }


  When("I ask for the current profile") { () =>
    stepsData.response = http("/rest/auth/profile")
      .asString
  }

  Then("""^repsonse should have (.*) equals to (.*)$""") { (path: String, value: String) =>
    val jsPath = JsString(path).validate[JsPath].get
    jsPath.read[String].reads(stepsData.responseJsonBody).get should be(value)
  }

}


