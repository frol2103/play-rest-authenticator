package steps

import javax.naming.Context
import javax.naming.directory.{BasicAttribute, BasicAttributes, InitialDirContext}
import steps.support.LdapUtils._

/**
  * Created by francois on 28/03/17.
  */
class LdapSteps extends Steps {

  Given("""^there is a user in ldap with email (.*) and password (.*)$""") { (email: String, password: String) =>
    addEntry(email, password)
  }

  Given("""^there is a user in ldap with email (.*) password (.*) firstname (.*) and lastname (.*)$""") {
    (email: String, password: String, firstname: String, lastname: String) =>
      addEntry(email, password, firstname, lastname)
  }

  Given("""I signin with ldap email (.*) and password (.*)$"""){(email:String, password:String) =>
    stepsData.response = http("/rest/auth/ldap/signin").postData(
      json("identifier"->parse(email),
        "password"->parse(password))
    ).asString
  }

  private def addEntry(email: String, password: String, firstname: String = "Foo", lastname: String = "Bar") = {
    val entry = new BasicAttributes()
    entry.put(new BasicAttribute("cn", firstname + " " + lastname))
    entry.put(new BasicAttribute("sn", lastname))
    entry.put(new BasicAttribute("givenName", firstname))
    entry.put(new BasicAttribute("mail", email))
    entry.put(new BasicAttribute("userPassword", "{MD5}" + digest("MD5", password)))
    entry.put {
      val oc = new BasicAttribute("objectClass");
      List("top", "person", "organizationalPerson", "inetOrgPerson")
        .foreach(oc.add)
      oc
    }

    val result = ldapDirContext.createSubcontext("uid=user1,dc=ldap,dc=example,dc=org", entry)
  }

  private def ldapDirContext = {
    val env = Map(
      Context.INITIAL_CONTEXT_FACTORY -> "com.sun.jndi.ldap.LdapCtxFactory",
      Context.PROVIDER_URL -> "ldap://ldap:389",
      Context.SECURITY_AUTHENTICATION -> "simple",
      Context.SECURITY_PRINCIPAL -> "cn=admin,dc=ldap,dc=example,dc=org",
      Context.SECURITY_CREDENTIALS -> "mysecretpassword"
    )
    val ctx = new InitialDirContext(toHashtable(env))
    ctx
  }

}



