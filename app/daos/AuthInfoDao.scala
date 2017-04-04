package daos

import com.mohiva.play.silhouette.api.{AuthInfo, LoginInfo}
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import models.User._
import models.{Profile, User}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.concurrent.Future

/**
  * Created by francois on 4/04/17.
  */
trait AuthInfoDao[T <: AuthInfo] extends DelegableAuthInfoDAO[T] with MongoPlayAppDao {
  def fromProfile(profile: Profile): Option[T]

  val profileMongoAccessor: String
  implicit val writes: Writes[T]

  val users = reactiveMongoApi.db.collection[JSONCollection]("users")

  def find(loginInfo: LoginInfo): Future[Option[T]] = for {
    user <- users.find(Json.obj(
      "profiles.loginInfo" -> loginInfo
    )).one[User]
  } yield user.flatMap(_.profiles.find(_.loginInfo == loginInfo)).flatMap(fromProfile)

  def add(loginInfo: LoginInfo, authInfo: T): Future[T] =
    users.update(Json.obj(
      "profiles.loginInfo" -> loginInfo
    ), Json.obj(
      "$set" -> Json.obj("profiles.$.passwordInfo" -> authInfo)
    )).map(_ => authInfo)

  def update(loginInfo: LoginInfo, authInfo: T): Future[T] =
    add(loginInfo, authInfo)

  def save(loginInfo: LoginInfo, authInfo: T): Future[T] =
    add(loginInfo, authInfo)

  def remove(loginInfo: LoginInfo): Future[Unit] =
    users.update(Json.obj(
      "profiles.loginInfo" -> loginInfo
    ), Json.obj(
      "$pull" -> Json.obj(
        "profiles" -> Json.obj("loginInfo" -> loginInfo)
      )
    )).map(_ => ())

}
