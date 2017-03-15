package steps

import cucumber.api.PendingException
import cucumber.api.java.en.{Then, When}
import org.scalatest.Matchers
import play.api.libs.json.Json
import steps.support.Config

import scalaj.http.Http

class HealthSteps extends Matchers with Config {

  @When("""^a request is made to the (.*) endpoint$""")
  def requestIsMadeToEndpoint(endpoint: String) {
    response = Http(s"$backendHost$endpoint")
      .timeout(connTimeoutMs = 1000, readTimeoutMs = 10000)
      .asString
  }

  @Then("""^a (\d+) status code is received$""")
  def statusCodeReceived(status: Int) = {
    response.code shouldBe status
  }


  @Then("""^the body of the response is "([^"]*)"$""")
  def bodyOfResponseIs(body: String) = {
    response.body should be(body)
  }



}
