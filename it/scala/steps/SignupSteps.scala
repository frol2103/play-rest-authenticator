package steps


import com.google.inject.Inject
import cucumber.api.java.en.Given
import org.scalatest.Matchers
import play.api.libs.json.Writes
import steps.support.{Config, StepsData}

import scalaj.http.Http


class SignupSteps @Inject()(stepsData : StepsData) extends Steps {


  @Given("""I signup with email (.*) and password (.*)$""")
  def iSignUp(email:String, password:String) = {
    stepsData.response = http("/rest/auth/signup").put(
      json("firstname"->"foo",
        "lastname"->"bar",
        "email"->email,
        "password"->password)
    ).asString
  }

  @Given("""I signin with email (.*) and password (.*)$""")
  def iSignIn(email:String, password:String) = {
    stepsData.response = http("/rest/auth/signin").postData(
      json("email"->email,
        "password"->password)
    ).asString
  }

}

class Steps extends Matchers with Config {

  def json[T](obj:T)(implicit writes : Writes[T]) : String = writes.writes(obj).toString()
  def json[T](values : (String, T)*)(implicit writes : Writes[T]) : String = json(values.toMap)


  def http(endpoint:String) = Http(s"$backendHost$endpoint")
      .header("Content-Type", "application/json;")
      .timeout(connTimeoutMs = 1000, readTimeoutMs = 10000)

}