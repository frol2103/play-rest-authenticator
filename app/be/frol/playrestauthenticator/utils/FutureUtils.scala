package be.frol.playrestauthenticator.utils

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

/**
  * Created by francois on 6/03/17.
  */
object FutureUtils {
  implicit def increasedFuture[T](t:Future[T]) = new Object{
    def zipMap[U](f: T=>Future[U])(implicit ec: ExecutionContext) :Future[(T,U)]= t.zip(t.flatMap(f))
  }
}
