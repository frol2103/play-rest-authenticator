package utils

import play.api.libs.functional.ContravariantFunctor
import play.api.libs.json.Writes

/**
  * Created by francois on 16/03/17.
  */
object WritesUtils {

  implicit val contravariantfunctorWrites:ContravariantFunctor[Writes] = new ContravariantFunctor[Writes] {

    def contramap[A,B](wa:Writes[A], f: B => A):Writes[B] = Writes[B]( b => wa.writes(f(b)) )

  }
}
