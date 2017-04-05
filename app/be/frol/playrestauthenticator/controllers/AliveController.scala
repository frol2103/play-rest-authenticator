package be.frol.playrestauthenticator.controllers

import javax.inject._

import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to check is app is alive
 */
@Singleton
class AliveController @Inject() extends Controller {

  def alive = Action {
    Ok("OK")
  }

}
