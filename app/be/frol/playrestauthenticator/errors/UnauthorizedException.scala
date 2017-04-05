package be.frol.playrestauthenticator.errors

/**
  * Created by francois on 9/03/17.
  */
class UnauthorizedException(msg:String) extends AuthenticationException("UNAUTHORIZED_EXCEPTION",msg) {}
