package utils

import scala.language.implicitConversions
/**
  * Created by francois on 6/03/17.
  */
object Utils {
  implicit def optionUtils[T](t:T) = new Object{
    def toOpt() :Option[T]= Option(t);
  }
}
