package steps

import cucumber.api.scala.{EN, ScalaDsl}
import netscape.javascript.JSObject
import org.scalatest.Matchers
import play.api.libs.json.Writes
import steps.support.Config

import scalaj.http.Http

class SignupSteps extends Steps {


  Given("""I signup with email (.*) and password (.*)$"""){ (email:String, password:String) =>
    response = http("/rest/auth/signup").put(
      json("firstname"->"foo",
        "lastname"->"bar",
        "email"->email,
        "password"->password)
    ).asString
    response.code should be (200)
  }

  Given("""I signin with email (.*) and password (.*)$"""){ (email:String, password:String) =>
    response = http("/rest/auth/signin").postData(
      json("email"->email,
        "password"->password)
    ).asString
    response.code should be (200)
  }

}

class Steps extends ScalaDsl with EN with Matchers with Config {

  def json[T](obj:T)(implicit writes : Writes[T]) : String = writes.writes(obj).toString()
  def json[T](values : (String, T)*)(implicit writes : Writes[T]) : String = json(values.toMap)


  def http(endpoint:String) = Http(s"$backendHost$endpoint")
      .header("Content-Type", "application/json;")
      .timeout(connTimeoutMs = 1000, readTimeoutMs = 10000)

}
