package steps

import cucumber.api.scala.{EN, ScalaDsl}
import org.joda.time.DateTime
import org.scalatest.Matchers
import play.api.libs.json.Writes
import play.api.test.FakeApplication
import steps.support.{Config, StepsData}

import scalaj.http.Http
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by francois on 18/03/17.
  */
class Steps extends Matchers with Config  with ScalaDsl with EN {

  val timeout = 2 seconds

  def fakeApp = FakeApplication(additionalConfiguration = Map("mongodb.uri" -> "mongodb://db:27017/demodb"))

  def json[T](obj:T)(implicit writes : Writes[T]) : String = writes.writes(obj).toString()
  def json[T](values : (String, T)*)(implicit writes : Writes[T]) : String = json(values.filterNot(_._2 == null).toMap)

  def parse(s:String) = if(s == "-") null else s

  def stepsData = StepsData

  def http(endpoint:String) = {
    Http(s"$backendHost$endpoint")
      .cookies(stepsData.cookies)
      .header("Content-Type", "application/json;")
      .timeout(connTimeoutMs = 1000, readTimeoutMs = 10000)
  }


  def parseDate(s:String) = s match {
    case "yesterday" => (new DateTime()).minusDays(1)
    case _ => throw new RuntimeException("unparsable date")
  }
}
