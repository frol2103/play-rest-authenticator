package steps

import be.frol.playrestauthenticator.daos.MongoUserTokenDao
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.modules.reactivemongo.json._

import scala.concurrent.Await

class TokenSteps extends Steps {


  Given("""^the signup token for (.*) expired (.*)$""") { (email: String, dateString: String) =>
    running(fakeApp) {
      val dao = new MongoUserTokenDao()
      val date = parseDate(dateString)
      val eventualTokens = dao.tokens.update(Json.obj("email" -> email), Json.obj("$set" -> Json.obj("expirationTime" -> date)))
      Await.result(eventualTokens.map(_.nModified), timeout) should be(1)
      eventualTokens.foreach(v => println("token : " + v))
    }
  }
}
