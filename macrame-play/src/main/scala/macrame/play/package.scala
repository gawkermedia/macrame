package macrame.play

import scala.util.control.NoStackTrace

package object enums {
   private[enums] def throwException(s : String) = throw new Exception(s) with NoStackTrace
   private[enums] def suppressUnusedWarning[A](a : A) : Unit = { val _ = a; () }
}
