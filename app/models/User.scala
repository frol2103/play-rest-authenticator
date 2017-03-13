package models

import java.util.UUID

import play.api.libs.json.Json

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.providers.OAuth1Info
import utils.Utils._
case class Profile(
  loginInfo:LoginInfo,
  confirmed: Boolean,
  email:Option[String],
  firstName: Option[String], 
  lastName: Option[String],
  fullName: Option[String], 
  passwordInfo:Option[PasswordInfo], 
  oauth1Info: Option[OAuth1Info]) {
}

case class User(id: UUID, profiles: List[Profile]) extends Identity {
  def profileFor(loginInfo:LoginInfo) = profiles.find(_.loginInfo == loginInfo)
  def fullName(loginInfo:LoginInfo) = profileFor(loginInfo).flatMap(u => u.fullName.orElse(List(u.firstName, u.lastName).flatten.mkString(" ").toOpt))
}

object User {
  implicit val passwordInfoJsonFormat = Json.format[PasswordInfo]
  implicit val oauth1InfoJsonFormat = Json.format[OAuth1Info]
  implicit val profileJsonFormat = Json.format[Profile]
  implicit val userJsonFormat = Json.format[User]
}
