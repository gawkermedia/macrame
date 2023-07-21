package macrame.enums

import macrame.EnumApi

/**
 * This trait provides conversion from an enumeration to String. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends AsString[Color]
 * }}}
 * This enables the following two ways to convert `Color` to a `String`.
 * {{{
 * Red.asString
 * Color.asString(Red) // returns "Red"
 * }}}
 */
trait AsString[E] { self : EnumApi[E] =>
   @inline def asString(`enum` : E) : String = asStringImpl(enum)
   implicit class AsStringOps(`enum` : E) {
      @inline def asString : String = self.asString(enum)
   }
}

/**
 * This trait provides conversion from String to an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends FromString[Color]
 * }}}
 * This creates the following function to convert `String` to `Option[Color].
 * {{{
 * Color.fromString("Red") // returns Some(Red)
 * }}}
 */
trait FromString[E] { self : EnumApi[E] =>
   @inline def fromString(str : String) : Option[E] = fromStringImpl(str)
}

/**
 * This trait provides the `String`/`E` conversions from `AsString` and `FromString`.
 * As with those traits you must extend the companion object of the enumeration.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends StringConverters[Color]
 * }}}
 */
trait StringConverters[E] extends AsString[E] with FromString[E] { self : EnumApi[E] => }

/**
 * This trait provides conversion from an enumeration to Int. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends AsInt[Color]
 * }}}
 * This enables the following two ways to convert `Color` to a `Int`.
 * {{{
 * Red.asInt
 * Color.asInt(Red) // returns 0
 * }}}
 */
trait AsInt[E] { self : EnumApi[E] =>
   @inline def asInt(`enum` : E) : Int = asIntImpl(enum)
   implicit class AsIntOps(`enum` : E) {
      @inline def asInt : Int = self.asInt(enum)
   }
}

/**
 * This trait provides conversion from Int to an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends FromInt[Color]
 * }}}
 * This creates the following function to convert `Int` to `Option[Color].
 * {{{
 * Color.fromInt(0) // returns Some(Red)
 * }}}
 */
trait FromInt[E] { self : EnumApi[E] =>
   @inline def fromInt(int : Int) : Option[E] = fromIntImpl(int)
}

/**
 * This trait provides the `Int`/`E` conversions from `AsInt` and `FromInt`.
 * As with those traits you must extend the companion object of the enumeration.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends IntConverters[Color]
 * }}}
 */
trait IntConverters[E] extends AsInt[E] with FromInt[E] { self : EnumApi[E] => }

/**
 * This trait provides conversion from an enumeration to Short. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends AsShort[Color]
 * }}}
 * This enables the following two ways to convert `Color` to a `Short`.
 * {{{
 * Red.asShort
 * Color.asShort(Red) // returns 0
 * }}}
 */
trait AsShort[E] { self : EnumApi[E] =>
   @inline def asShort(`enum` : E) : Short = asShortImpl(enum)
   implicit class AsShortOps(`enum` : E) {
      @inline def asShort : Short = self.asShort(enum)
   }
}

/**
 * This trait provides conversion from Short to an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends FromShort[Color]
 * }}}
 * This creates the following function to convert `Short` to `Option[Color].
 * {{{
 * Color.fromShort(0 : Short) // returns Some(Red)
 * }}}
 */
trait FromShort[E] { self : EnumApi[E] =>
   @inline def fromShort(short : Short) : Option[E] = fromShortImpl(short)
}

/**
 * This trait provides the `Short`/`E` conversions from `AsShort` and `FromShort`.
 * As with those traits you must extend the companion object of the enumeration.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends ShortConverters[Color]
 * }}}
 */
trait ShortConverters[E] extends AsShort[E] with FromShort[E] { self : EnumApi[E] => }

/**
 * This trait provides conversion from an enumeration to Long. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends AsLong[Color]
 * }}}
 * This enables the following two ways to convert `Color` to a `Long`.
 * {{{
 * Red.asLong
 * Color.asLong(Red) // returns 0L
 * }}}
 */
trait AsLong[E] { self : EnumApi[E] =>
   @inline def asLong(`enum` : E) : Long = asLongImpl(enum)
   implicit class AsLongOps(`enum` : E) {
      @inline def asLong : Long = self.asLong(enum)
   }
}

/**
 * This trait provides conversion from Long to an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends FromLong[Color]
 * }}}
 * This creates the following function to convert `Long` to `Option[Color].
 * {{{
 * Color.fromLong(0L) // returns Some(Red)
 * }}}
 */
trait FromLong[E] { self : EnumApi[E] =>
   @inline def fromLong(long : Long) : Option[E] = fromLongImpl(long)
}

/**
 * This trait provides the `Long`/`E` conversions from `AsLong` and `FromLong`.
 * As with those traits you must extend the companion object of the enumeration.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends LongConverters[Color]
 * }}}
 */
trait LongConverters[E] extends AsLong[E] with FromLong[E] { self : EnumApi[E] => }

/**
 * This trait provides the numeric/enumeration conversions from `IntConverters`, `ShortConverters`, and `LongConverters`.
 * As with those traits you must extend the companion object of the enumeration.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends NumericConverters[Color]
 * }}}
 */
trait NumericConverters[E] extends LongConverters[E] with ShortConverters[E] with IntConverters[E] { self : EnumApi[E] => }

/**
 * This trait provides an `Ordering` instance for an enumeration as well
 * as functions which use that order. It works by extending the companion
 * object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends Ordered[Color]
 * }}}
 * This enables the following functions (and more).
 * {{{
 * Red < Blue // true
 * Blue.next
 * Color.next(Blue) // yellow
 * Yellow >= Yellow // true
 * Color.first // Red
 * Color.last // Yellow
 * }}}
 */
trait Ordered[E] { self : EnumApi[E] =>
   @inline implicit val ordering : Ordering[E] = orderingImpl
   @inline def next(`enum` : E) : Option[E] = nextImpl(enum)
   @inline def prev(`enum` : E) : Option[E] = prevImpl(enum)
   @inline def first : E = firstImpl
   @inline def last : E = lastImpl
   implicit class OrderedOps(`enum` : E) {
      @inline def next : Option[E] = self.next(enum)
      @inline def prev : Option[E] = self.prev(enum)
      @inline def <(other : E) = ordering.lt(enum, other)
      @inline def <=(other : E) = ordering.lteq(enum, other)
      @inline def >(other : E) = ordering.gt(enum, other)
      @inline def >=(other : E) = ordering.gteq(enum, other)
   }
}

/**
 * This traits provides the same interface as `Ordered` as well as the modular
 * ordering functions. As with that trait it works by extending the companion
 * object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends OrderedModular[Color]
 * }}}
 * This enables the following functions.
 * {{{
 * Red.prevMod
 * Color.prevMod(Red) // Yellow
 * Yellow.nextMod
 * Color.nextMod(Yellow) // Red
 * }}}
 */
trait OrderedModular[E] extends Ordered[E] { self : EnumApi[E] =>
   @inline def nextMod(`enum` : E) : E = nextModImpl(enum)
   @inline def prevMod(`enum` : E) : E = prevModImpl(enum)
   implicit class OrderedModularOps(`enum` : E) {
      @inline def nextMod : E = self.nextMod(enum)
      @inline def prevMod : E = self.prevMod(enum)
   }
}

/**
 * This traits provides *all* of the auto-generated functions in `EnumApi`.
 * It is rare that one actually needs all such functions and it is recommended
 * that you use a smaller subset of these functions as provided by the other traits
 * in the `macrame.enums` namespace.
 */
trait All[E] extends StringConverters[E] with NumericConverters[E] with OrderedModular[E] { self : EnumApi[E] =>
   @inline def values : Set[E] = valuesImpl
}
