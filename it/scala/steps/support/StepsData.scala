package steps.support
import cucumber.runtime.java.guice.ScenarioScoped
import play.api.libs.json.{JsValue, Json}

import scalaj.http.HttpResponse;

/**
  * Created by francois on 14/03/17.
  */
object StepsData
{
  var response:HttpResponse[_] = null



  def responseJsonBody : JsValue= {
    Option(response)
      .map(_.body.toString)
      .map(Json.parse)
      .getOrElse(throw new RuntimeException("No response"))
  }
}