package daos

import com.mohiva.play.silhouette.api.util.PasswordInfo
import models.User._
import models.{Profile, User}
import play.api.libs.json._

class PasswordInfoDao extends AuthInfoDao[PasswordInfo] {
  override def fromProfile(profile: Profile): Option[PasswordInfo] = profile.passwordInfo

  override val profileMongoAccessor: String = "passwordInfo"
  override implicit val writes: Writes[PasswordInfo] = passwordInfoJsonFormat
}


