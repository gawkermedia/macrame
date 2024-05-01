import language.experimental.macros
import reflect.macros.whitebox.Context
import macrame.{ internal => fn }

package object macrame {

   /**
    * Logs the source code of the given expression to the console during
    * compliation.
    */
   def trace[A](a : A) : A = macro Impl.trace[A]

   /** A list of all members of type `T` in the given object. */
   def members[T](obj : Object) : List[T] = macro Impl.members[T]

   /**
    * A map of all members of type `T` in the given object, keyed by the name of the member.
    */
   def memberMap[F](obj : Object) : Map[String, F] = macro Impl.memberMap[F]

   private object Impl {

      def trace[A](c : Context)(a : c.Expr[A]) : c.Expr[A] = {
         import c.universe._
         c.info(
            a.tree.pos,
            "trace output\n   " + show(a.tree) + "\nfor position:\n",
            true)
         a
      }

      def members[T : c.WeakTypeTag](c : Context)(obj : c.Expr[Object]) : c.Expr[List[T]] =
         fn.sequenceExpr(c)(
            fn.members[T](c)(obj)
               .map(s => fn.renderName(s.name))
               .map(n => c.Expr[T](c.universe.Select(obj.tree, c.universe.TermName(n))))
         )

      def memberMap[T : c.WeakTypeTag](c : Context)(obj : c.Expr[Object]) : c.Expr[Map[String, T]] = {
         import c.universe._

         val tups = fn.sequenceExpr(c)(fn.members(c)(obj)
            .map(_.name.decodedName.toString.trim)
            .map(n =>
               // ("n", obj.n)
               c.Expr[(String, T)](
                  Apply(Select(Ident(TermName("Tuple2")), TermName("apply")), List(
                     Literal(Constant(n)),
                     Select(obj.tree, TermName(n)))
                  )
               )
            )
         )
         // List(("a", obj.a), ("b", obj.b), ...).toMap
         reify { tups.splice.toMap }
      }
   }
}
