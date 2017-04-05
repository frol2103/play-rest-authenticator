package be.frol.playrestauthenticator.daos

import be.frol.playrestauthenticator.models.{Profile, User}

import scala.concurrent.Future
import com.mohiva.play.silhouette.api.util.PasswordInfo
import play.api.libs.json._
class PasswordInfoDao extends AuthInfoDao[PasswordInfo] {
  override def fromProfile(profile: Profile): Option[PasswordInfo] = profile.passwordInfo
import User._

  override val profileMongoAccessor: String = "passwordInfo"
  override implicit val writes: Writes[PasswordInfo] = passwordInfoJsonFormat
}


