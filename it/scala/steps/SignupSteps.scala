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

class Steps extends Matchers with Config  with ScalaDsl with EN {

  def json[T](obj:T)(implicit writes : Writes[T]) : String = writes.writes(obj).toString()
  def json[T](values : (String, T)*)(implicit writes : Writes[T]) : String = json(values.filterNot(_._2 == null).toMap)

  def parse(s:String) = if(s == "-") null else s

  def stepsData = StepsData

  def http(endpoint:String) = Http(s"$backendHost$endpoint")
      .header("Content-Type", "application/json;")
      .timeout(connTimeoutMs = 1000, readTimeoutMs = 10000)

}
