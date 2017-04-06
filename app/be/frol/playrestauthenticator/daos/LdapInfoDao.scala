package be.frol.playrestauthenticator.daos

import be.frol.playrestauthenticator.models.Profile
import be.frol.playrestauthenticator.providers.LdapInfo
import play.api.libs.json._
import be.frol.playrestauthenticator.models.User._
import com.mohiva.play.silhouette.api.LoginInfo
import play.Logger

import scala.concurrent.Future

class LdapInfoDao extends AuthInfoDao[LdapInfo] {
  override def fromProfile(profile: Profile): Option[LdapInfo] = profile.ldapInfo

  override val profileMongoAccessor: String = "ldapInfo"
  override implicit val writes: Writes[LdapInfo] = ldapInfoJsonFormat

  override def save(loginInfo: LoginInfo, authInfo: LdapInfo): Future[LdapInfo] = {
    super.save(loginInfo, authInfo)
  }
}


