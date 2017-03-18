package steps


import com.google.inject.Inject
import cucumber.api.java.en.Given
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers
import play.api.libs.json.Writes
import steps.support.{Config, StepsData}

import scalaj.http.Http


class SignupSteps extends Steps {


  Given("""I signup with email (.*) and password (.*)$"""){(email:String, password:String) =>
    stepsData.response = http("/rest/auth/signup").put(
      json("firstname"->"foo",
        "lastname"->"bar",
        "email"->parse(email),
        "password"->parse(password))
    ).asString
    println(stepsData.response)
    println(password)
  }

  Given("""I signin with email (.*) and password (.*)$"""){(email:String, password:String) =>
    stepsData.response = http("/rest/auth/signin").postData(
      json("identifier"->parse(email),
        "password"->parse(password))
    ).asString
  }

}


