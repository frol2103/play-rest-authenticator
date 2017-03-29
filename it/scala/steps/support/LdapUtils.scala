package steps.support

import java.security.MessageDigest
import java.util

import sun.misc.BASE64Encoder

/**
  * Created by francois on 29/03/17.
  */
object LdapUtils {

  def toHashtable(env: Map[String, String]) = {
    val envTable = new util.Hashtable[String, String]()
    env.foreach { case (k, v) => envTable.put(k, v) }
    envTable
  }

  def digest(algorithm:String, password: String) = {
    val md = MessageDigest.getInstance(algorithm)
    md.update(password.getBytes())
    val b = md.digest()
    val encoder = new BASE64Encoder()
    encoder.encode(b)
  }

}
