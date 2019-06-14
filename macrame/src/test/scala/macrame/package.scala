package macrame

object Helpers {
   private[macrame] def suppressUnusedWarning[A](a : A) : Unit = { val _ = a; () }
   private[macrame] def suppressUnusedWarning[A] : Unit = ()
}
