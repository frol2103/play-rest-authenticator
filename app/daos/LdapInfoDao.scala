package daos

import com.mohiva.play.silhouette.api.util.PasswordInfo
import models.Profile
import models.User._
import play.api.libs.json._
import providers.LdapInfo

class LdapInfoDao extends AuthInfoDao[LdapInfo] {
  override def fromProfile(profile: Profile): Option[LdapInfo] = profile.ldapInfo

  override val profileMongoAccessor: String = "ldapInfo"
  override implicit val writes: Writes[LdapInfo] = ldapInfoJsonFormat
}


