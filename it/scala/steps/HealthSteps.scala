package steps

import cucumber.api.PendingException
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers
import play.api.libs.json.Json
import steps.support.Config

import scalaj.http.Http

class HealthSteps extends ScalaDsl with EN with Matchers with Config {

  When("""^a request is made to the (.*) endpoint$""") { (endpoint: String) =>
    response = Http(s"$backendHost$endpoint")
      .timeout(connTimeoutMs = 1000, readTimeoutMs = 10000)
      .asString
  }

  Then("""^a (\d+) status code is received$""") { (status: Int) =>
    response.code shouldBe status
  }


  Then("""^the body of the response is "([^"]*)"$"""){ (body: String) =>
    response.body should be(body)
  }



}
