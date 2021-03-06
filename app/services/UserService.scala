package services

import java.util.UUID
import javax.inject._

import scala.concurrent.Future
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.collection.JSONCollection
import daos.UserDao
import errors.UserAlreadyExistException
import models.{Profile, User}

class UserService @Inject() (userDao:UserDao) extends IdentityService[User] {
  def retrieve(loginInfo:LoginInfo):Future[Option[User]] = userDao.find(loginInfo)

  def save(user:User) = userDao.save(user)

  def saveNewUser(profile: Profile) : Future[User] = {
    retrieve(profile.loginInfo).flatMap{
      case Some(_) => Future.failed(new UserAlreadyExistException("User already exists for " + profile.loginInfo.providerKey))
      case None => userDao.save(fromProfile(profile))
    }
  }


  def find(id:UUID) = userDao.find(id)
  def confirm(loginInfo:LoginInfo) = userDao.confirm(loginInfo)
  def link(user:User, socialProfile:CommonSocialProfile) = {
    val profile = toProfile(socialProfile)
    if (user.profiles.exists(_.loginInfo == profile.loginInfo)) Future.successful(user) else userDao.link(user, profile)
  }

  def save(socialProfile:CommonSocialProfile) : Future[User] = {
    val profile = toProfile(socialProfile)
    userDao.find(profile.loginInfo).flatMap {
      case None => userDao.save(fromProfile(profile))
      case Some(user) => userDao.update(profile)
    }
  }

  private def fromProfile(profile: Profile) = {
    User(UUID.randomUUID(), List(profile))
  }

  private def toProfile(p:CommonSocialProfile) = Profile(
    loginInfo = p.loginInfo,
    confirmed = true,
    email = p.email,
    firstName = p.firstName,
    lastName = p.lastName,
    fullName = p.fullName,
    passwordInfo = None,
    oauth1Info = None
  )
}
