package steps

import cucumber.api.java.Before
import play.api.libs.json.JsValue._
import play.api.libs.json.{JsString, Json}

class BasicSteps extends Steps{

  @Before
  def beforeHook(): Unit = {
    stepsData.reset()
  }

  When("""^a request is made to the (.*) endpoint$""") {( endpoint: String) =>
    stepsData.response = http(endpoint).asString
  }

  Then("""^a (\d+) status code is received$""") {( status: Int) =>
    stepsData.response.code should be(status)
  }

  Then("""^an? (.*) error should be thrown$"""){(code: String) =>
    stepsData.responseJsonBody.\("code").get should be(JsString(code))
  }

  Then("""^there is an error (.*) for (.*)$"""){(error: String, path:String) =>
    implicit val format = Json.format[ErrorForJsPathBasic]
    val exceptions = stepsData.responseJsonBody.\("inputExceptions").validate[Seq[ErrorForJsPathBasic]].get
    exceptions.find(_.jsPath == path).flatMap(_.errors.find(_ == error)).isDefined should be(true)
  }


  Then("""^the body of the response is "([^"]*)"$"""){( body: String) =>
    stepsData.response.body should be(body)
  }



}
case class ErrorForJsPathBasic(jsPath:String, errors:Seq[String])
