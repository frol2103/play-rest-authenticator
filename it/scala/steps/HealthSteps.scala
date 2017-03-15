package steps

import com.google.inject.Inject
import cucumber.api.PendingException
import cucumber.api.java.en.{Then, When}
import org.scalatest.Matchers
import play.api.libs.json.Json
import steps.support.{Config, StepsData}

import scalaj.http.Http

class HealthSteps @Inject() (stepsData: StepsData) extends Steps {

  @When("""^a request is made to the (.*) endpoint$""")
  def requestIsMadeToEndpoint(endpoint: String) {
    stepsData.response = http(endpoint).asString
  }

  @Then("""^a (\d+) status code is received$""")
  def statusCodeReceived(status: Int) = {
    stepsData.response.code should be(status)
  }


  @Then("""^the body of the response is "([^"]*)"$""")
  def bodyOfResponseIs(body: String) = {
    stepsData.response.body should be(body)
  }



}
