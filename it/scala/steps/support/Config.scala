package steps.support

import play.Play

import scalaj.http._

trait Config {
  val backendHost = sys.env.get("backend_url").getOrElse("http://app:9000")
  var response: HttpResponse[String] = null
}
