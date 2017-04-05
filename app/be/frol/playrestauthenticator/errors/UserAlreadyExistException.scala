package be.frol.playrestauthenticator.errors

/**
  * Created by francois on 9/03/17.
  */
class UserAlreadyExistException(msg:String) extends AuthenticationException("USER_EXISTS",msg) {

}
