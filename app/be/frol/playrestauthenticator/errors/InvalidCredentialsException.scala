package be.frol.playrestauthenticator.errors

/**
  * Created by francois on 9/03/17.
  */
class InvalidCredentialsException(msg:String, cause: Option[Throwable] = None) extends AuthenticationException("INVALID_CREDENTIALS",msg,cause) {}
