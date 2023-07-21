package macrame.scalaz.enums

import macrame.EnumApi

import scala.annotation.tailrec

import scalaz._

/**
 * This trait provides an instance of `Show` for an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends IsShow[Color]
 * }}}
 */
trait IsShow[E] { self : EnumApi[E] =>
   implicit val show : Show[E] = Show.shows[E](asStringImpl _)
}

trait IsEqual[E] { self : EnumApi[E] =>
   implicit val equal : Equal[E] = Equal.equal[E](_ == _)
}

trait IsOrder[E] { self : EnumApi[E] =>
   implicit val order : Order[E] = new Order[E] {
      override def equal(x : E, y : E) : Boolean = x == y
      def order(x : E, y : E) : Ordering =
         if (x == y)
            Ordering.EQ
         else if (orderingImpl.gt(x, y))
            Ordering.GT
         else
            Ordering.LT
   }
}

trait IsEnum[E] { self : EnumApi[E] =>
   implicit val `enum` : Enum[E] = new Enum[E] {
      override def equal(x : E, y : E) : Boolean = x == y
      def order(x : E, y : E) : Ordering =
         if (x == y)
            Ordering.EQ
         else if (orderingImpl.gt(x, y))
            Ordering.GT
         else
            Ordering.LT
      def pred(e : E) = prevModImpl(e)
      def succ(e : E) = nextModImpl(e)
      override val max = Some(lastImpl)
      override val min = Some(firstImpl)
   }
}

/**
 * This trait provides an instance of `Semigroup` for an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Mod3 {
 *   Zero
 *   One
 *   Two
 * }
 * object Mod3 extends IsSemigroup[Mod3]
 * }}}
 */
trait IsSemigroup[E] { self : EnumApi[E] =>
   implicit lazy val semigroup : Semigroup[E] = new Semigroup[E] {
      def append(x : E, y : => E) : E = nextN(x, asIntImpl(y))

      @tailrec
      private def nextN(e : E, n : Int) : E =
         if (n == 0)
            e
         else
            nextN(nextModImpl(e), n - 1)
   }
}

trait IsMonoid[E] { self : EnumApi[E] =>
   implicit lazy val monoid : Monoid[E] = new Monoid[E] {
      val zero : E = firstImpl
      def append(x : E, y : => E) : E = nextN(x, asIntImpl(y))

      @tailrec
      private def nextN(e : E, n : Int) : E =
         if (n <= 0)
            e
         else
            nextN(nextModImpl(e), n - 1)
   }
}

trait All[E] extends IsMonoid[E] with IsEnum[E] with IsShow[E] { self : EnumApi[E] => }
