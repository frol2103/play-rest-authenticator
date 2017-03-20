package steps.support
import java.net.HttpCookie

import play.api.libs.json.{JsValue, Json}

import scalaj.http.HttpResponse;

/**
  * Created by francois on 14/03/17.
  */
object StepsData
{

  var _response: HttpResponse[_] = null
  var cookies: Seq[HttpCookie] = Seq()

  def response = _response

  def response_=(r: HttpResponse[_]) = {
    this._response = r
    this.cookies = r.cookies
  }


  def responseJsonBody : JsValue= {
    Option(response)
      .map(_.body.toString)
      .map(Json.parse)
      .getOrElse(throw new RuntimeException("No response"))
  }
}