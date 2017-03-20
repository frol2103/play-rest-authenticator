package utils

import org.scalatest.{FlatSpec, FunSuite, Matchers}
import play.api.libs.json.{JsPath, JsString, JsValue, Json}

/**
  * Created by francois on 20/03/17.
  */
class JsonUtilsTest extends FlatSpec with Matchers {

  "JsPath format" should "serialize and unserialize root object" in {
    val path = JsPath
    testJsPathFormat(path, JsString("."))
  }

  it should "serialize and unserialize path of one children" in {
    val path = JsPath \ "children"
    testJsPathFormat(path, JsString(".children"))
  }

  def testJsPathFormat(path: JsPath, json: JsValue): Any = {
    val writed = JsonUtils.jsPathFormat.writes(path)
    Json.toJson(writed) should be(json)
    JsonUtils.jsPathFormat.reads(writed).get should be(path)
  }
}
