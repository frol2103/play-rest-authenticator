package be.frol.playrestauthenticator.controllers

import be.frol.playrestauthenticator.models.User
import com.mohiva.play.silhouette.api.{Environment, LoginInfo}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import play.api.mvc.{Controller, RequestHeader}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by francois on 6/04/17.
  */
trait SignInTrait extends Controller{

  def env: Environment[User, CookieAuthenticator]

  def signInLoginInfo(loginInfo: Future[LoginInfo])(implicit request: RequestHeader,executionContext: ExecutionContext) = {
    loginInfo
      .flatMap(env.authenticatorService.create(_))
      .flatMap(env.authenticatorService.init)
      .flatMap(env.authenticatorService.embed(_, Ok))
  }
}
