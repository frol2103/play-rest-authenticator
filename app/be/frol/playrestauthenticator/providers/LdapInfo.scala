package be.frol.playrestauthenticator.providers

import com.mohiva.play.silhouette.api.AuthInfo

/**
  * The ldap details.
  *
  * @param uid the uid used in the ldap
  */
case class LdapInfo(uid:String) extends AuthInfo

