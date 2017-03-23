package controllers.credentials

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

/**
  * Created by francois on 23/03/17.
  */
trait CredentialsAction {

  def loginInfo(email: String) = LoginInfo(CredentialsProvider.ID, email)

}
