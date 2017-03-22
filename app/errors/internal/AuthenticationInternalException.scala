package errors.internal

/**
  * Created by francois on 21/03/17.
  */
class AuthenticationInternalException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message, cause.getOrElse(null)) {

}
