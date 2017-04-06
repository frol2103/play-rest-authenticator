/**
  * Original work: SecureSocial (https://github.com/jaliss/securesocial)
  * Copyright 2013 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
  *
  * Derivative work: Silhouette (https://github.com/mohiva/play-silhouette)
  * Modifications Copyright 2015 Mohiva Organisation (license at mohiva dot com)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package be.frol.playrestauthenticator.providers

import java.util
import javax.inject.Inject
import javax.naming.Context
import javax.naming.directory.{InitialDirContext, SearchControls}

import be.frol.playrestauthenticator.errors.AuthenticationException
import be.frol.playrestauthenticator.models.Profile
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Credentials, ExecutionContextProvider, PasswordHasher}
import com.mohiva.play.silhouette.impl.exceptions.{IdentityNotFoundException, InvalidPasswordException}
import be.frol.playrestauthenticator.providers.LdapProvider._
import be.frol.playrestauthenticator.services.UserService

import scala.concurrent.{ExecutionContext, Future}

/**
  * A provider for authenticating with ldap.
  *
  */
class LdapProvider @Inject()(
                              authInfoRepository: AuthInfoRepository,
                              userService: UserService
                            )(implicit val executionContext: ExecutionContext)
  extends Provider with ExecutionContextProvider {

  /**
    * Gets the provider ID.
    *
    * @return The provider ID.
    */
  override def id = ID


  private def findOrSave(loginInfo: LoginInfo)(recover : => Profile) : Future[LdapInfo] = {
    authInfoRepository.find[LdapInfo](loginInfo).flatMap{
      case Some(s) => Future.successful(s)
      case None => {
        val ldapInfo = ldapProfile(loginInfo)
        userService.saveNewUser(ldapInfo).map(_ => ldapInfo.ldapInfo.get)
      }
    }
  }

  def checkPassword(authInfo: LdapInfo, credentials: Credentials) = {
    contextFor(authInfo.uid, credentials.password)
  }

  /**
    * Authenticates a user with its credentials.
    *
    * @param credentials The credentials to authenticate with.
    * @return The login info if the authentication was successful, otherwise a failure.
    */
  def authenticate(credentials: Credentials): Future[LoginInfo] = {
    val loginInfo = LoginInfo(id, credentials.identifier)
    findOrSave(loginInfo)(ldapProfile(loginInfo))
      .map{authInfo => checkPassword(authInfo, credentials); loginInfo}
  }

  private def ldapProfile(loginInfo: LoginInfo) : Profile= {
    val ctx = contextFor("cn=admin,dc=ldap,dc=example,dc=org", "mysecretpassword")
    val ctrls = new SearchControls();
    ctrls.setReturningAttributes(Array("givenName", "sn", "cn", "givenName", "memberOf", "mail"));
    ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    val answers = ctx.search("dc=ldap,dc=example,dc=org", "(mail=" + loginInfo.providerKey + ")", ctrls);

    if(!answers.hasMore) throw new IdentityNotFoundException("Identity not found in ldap")

    val result = answers.nextElement();
    def getAttribute(key: String): Option[String] = Some{result.getAttributes.get(key).get().toString}

    Profile(loginInfo,
      true,
      Some(loginInfo.providerID),
      getAttribute("givenName"),
      getAttribute("sn"),
      getAttribute("cn"),
      ldapInfo = Some(LdapInfo(result.getNameInNamespace())));
  }


  private def contextFor(principal: String, password: String) = {
    val env = Map(
      Context.INITIAL_CONTEXT_FACTORY -> "com.sun.jndi.ldap.LdapCtxFactory",
      Context.PROVIDER_URL -> "ldap://ldap:389",
      Context.SECURITY_AUTHENTICATION -> "simple",
      Context.SECURITY_PRINCIPAL -> principal,
      Context.SECURITY_CREDENTIALS -> password
    )
    try{
      new InitialDirContext(toHashtable(env))
    } catch {
      case e: javax.naming.AuthenticationException => throw new InvalidPasswordException("Invalid password", e)
    }
  }
}


/**
  * The companion object.
  */
object LdapProvider {

  /**
    * The error messages.
    */
  val UnknownCredentials = "[Silhouette][%s] Could not find auth info for given credentials"
  val InvalidPassword = "[Silhouette][%s] Passwords does not match"

  /**
    * The provider constants.
    */
  val ID = "ldap"


  def toHashtable(env: Map[String, String]) = {
    val envTable = new util.Hashtable[String, String]()
    env.foreach { case (k, v) => envTable.put(k, v) }
    envTable
  }


}