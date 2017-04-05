package be.frol.playrestauthenticator.daos

import play.api.Play.current
import play.modules.reactivemongo.ReactiveMongoApi

/**
  * Created by francois on 22/03/17.
  */
trait MongoPlayAppDao {
  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]
}
