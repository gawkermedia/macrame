package macrame

import language.experimental.macros

import reflect.macros.blackbox.Context

import macrame.{ function ⇒ fn }

object DebugMacros {
   def hello() : Unit = macro hello_impl

   def hello_impl(c : Context)() : c.Expr[Unit] = {
      import c.universe._
      reify { println("Hello World!") }
   }
}

package object compile {

   /**
    * Prints the source code of the given expression to the console during
    * compliation.
    */
   def trace[A](a : A) : A = macro Impl.trace[A]

   def members[T](obj : Object) : List[T] = macro Impl.members[T]

   def memberProduct[F](n : String ⇒ String)(obj : Object) : Product = macro Impl.memberProduct[F]

   def memberMap[F](obj : Object) : Map[String, F] = macro Impl.memberMap[F]

   def mixIn[C, T](_class : C)(props : Any*) : C with T = macro Impl.mixIn[C, T]

   implicit class RegexStringContext(sc : StringContext) {
      def r() : scala.util.matching.Regex = macro Impl._regex
      // def unapplySeq(args : String) : scala.util.matching.Regex = macro Impl.Regex
   }
   def regex(s : String) : scala.util.matching.Regex = macro Impl.regex

   private object Impl {

      def trace[A](c : Context)(a : c.Expr[A]) = {
         import c.universe._
         println("//////////////////\n" +
            "// TRACE OUTPUT //\n" +
            "//////////////////\n" +
            show(a.tree) +
            "\n//////////////////")
         a
      }

      def _regex(c : Context)() : c.Expr[scala.util.matching.Regex] = {
         import c.universe._

         val s = c.prefix.tree match {
            case Apply(_, List(Apply(_, List(sc)))) ⇒ c.Expr[String](sc)
            case x                                  ⇒ c.abort(c.enclosingPosition, "unexpected tree: " + show(x))
         }

         def getPoint(msg : String) : Int =
            msg.split("\n")(2).indexOf('^')

         try {
            s.tree match {
               case Literal(Constant(string : String)) ⇒
                  string.r
                  c.universe.reify(s.splice.r)
               case _ ⇒ c.abort(s.tree.pos, "Arguments to regex must be string literals")
            }
         } catch {
            case e : java.util.regex.PatternSyntaxException ⇒
               val pos = s.tree.pos.withPoint(getPoint(e.getMessage) + s.tree.pos.point)
               c.abort(pos, "Invalid Regex: " + e.getMessage.split("\n").head)
         }
      }

      def regex(c : Context)(s : c.Expr[String]) : c.Expr[scala.util.matching.Regex] = {
         import c.universe._

         def getPoint(msg : String) : Int =
            msg.split("\n")(2).indexOf('^')

         try {
            s.tree match {
               case Literal(Constant(string : String)) ⇒
                  string.r
                  c.universe.reify(s.splice.r)
               case _ ⇒ c.abort(s.tree.pos, "Arguments to regex must be string literals")
            }
         } catch {
            case e : java.util.regex.PatternSyntaxException ⇒
               val pos = s.tree.pos.withPoint(getPoint(e.getMessage) + s.tree.pos.point + 1)
               c.abort(pos, "Invalid Regex: " + e.getMessage.split("\n").head)
         }
      }

      private def sequenceExpr[T : c.WeakTypeTag](c : Context)(expressions : Traversable[c.Expr[T]]) : c.Expr[List[T]] =
         expressions.foldLeft(c.universe.reify(List.empty[T])) {
            (acc, expr) ⇒
               c.universe.reify(expr.splice :: acc.splice)
         }

      def members[T : c.WeakTypeTag](c : Context)(obj : c.Expr[Object]) : c.Expr[List[T]] =
         fn.sequenceExpr(c)(
            fn.members[T](c)(obj)
               .map(s ⇒ fn.renderName(s.name))
               .map(n ⇒ c.Expr[T](c.universe.Select(obj.tree, c.universe.TermName(n))))
         )

      def memberProduct[T : c.WeakTypeTag](c : Context)(n : c.Expr[String ⇒ String])(obj : c.Expr[Object]) : c.Expr[Product] = {
         import c.universe._
         val ms = fn.members[T](c)(obj)
            .map(s ⇒ fn.renderName(s.name))
            .map(n ⇒ c.universe.Select(obj.tree, c.universe.TermName(n)))
         c.Expr[Product](Apply(Select(Ident(TermName("Tuple" + ms.toList.length.toString)), TermName("apply")), ms.toList))
      }

      def memberMap[T : c.WeakTypeTag](c : Context)(obj : c.Expr[Object]) : c.Expr[Map[String, T]] = {
         import c.universe._

         val tups = sequenceExpr(c)(fn.members(c)(obj)
            .map(_.name.decodedName.toString.trim)
            .map(n ⇒
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

      def mixIn[C, T](c : Context)(_class : c.Expr[C])(props : Any*) : c.Expr[C with T] = {
         import c.universe._
         ???
      }
   }
}
