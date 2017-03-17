package steps

import com.google.inject.Inject
import cucumber.api.scala.{EN, ScalaDsl}

import org.scalatest.Matchers
import play.api.libs.json.{JsString, Json}
import play.api.mvc.BodyParsers
import steps.support.{Config, StepsData}
import utils.bodyparser.JsonParser
import play.api.libs.json.JsValue._

import scalaj.http.Http

class HealthSteps extends Steps{

  When("""^a request is made to the (.*) endpoint$""") {( endpoint: String) =>
    stepsData.response = http(endpoint).asString
  }

  Then("""^a (\d+) status code is received$""") {( status: Int) =>
    stepsData.response.code should be(status)
  }

  Then("""^a (.*) error should be thrown$"""){(code: String) =>
    stepsData.responseJsonBody.\("code").get should be(JsString(code))
  }

  Then("""^the body of the response is "([^"]*)"$"""){( body: String) =>
    stepsData.response.body should be(body)
  }



}
