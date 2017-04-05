package be.frol.playrestauthenticator.daos

import be.frol.playrestauthenticator.models.Profile
import be.frol.playrestauthenticator.providers.LdapInfo
import play.api.libs.json._
import be.frol.playrestauthenticator.models.User._

class LdapInfoDao extends AuthInfoDao[LdapInfo] {
  override def fromProfile(profile: Profile): Option[LdapInfo] = profile.ldapInfo

  override val profileMongoAccessor: String = "ldapInfo"
  override implicit val writes: Writes[LdapInfo] = ldapInfoJsonFormat
}


