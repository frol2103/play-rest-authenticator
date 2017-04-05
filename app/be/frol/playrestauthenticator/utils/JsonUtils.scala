package be.frol.playrestauthenticator.utils

import play.api.libs.functional.ContravariantFunctor
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{Format, JsPath, Reads, Writes}

/**
  * Created by francois on 16/03/17.
  */
object JsonUtils {

  implicit val contravariantfunctorWrites: ContravariantFunctor[Writes] = new ContravariantFunctor[Writes] {
    def contramap[A, B](wa: Writes[A], f: B => A): Writes[B] = Writes[B](b => wa.writes(f(b)))
  }

  val jsPathWrites: Writes[JsPath] = implicitly[Writes[String]].contramap[JsPath](v => """^obj\.?""".r.replaceFirstIn(v.toJsonString,"."))

  val jsPathReads: Reads[JsPath] = implicitly[Reads[String]].map(_.split("\\.")
    .drop(1).foldLeft[JsPath](JsPath)(_ \ (_)))

  implicit val jsPathFormat = Format(jsPathReads, jsPathWrites)
}
