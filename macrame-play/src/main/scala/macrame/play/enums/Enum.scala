package macrame.play.enums

import macrame.EnumApi

import play.api.libs.json._
import play.api.mvc.{ PathBindable, QueryStringBindable }

import scala.util.Try

/**
 * This trait provides an instance of `Writes` for an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends AsJson[Color]
 * }}}
 */
trait AsJson[E] { self : EnumApi[E] =>
   implicit val writes : Writes[E] = new Writes[E] {
      def writes(enum : E) : JsValue = JsString(asStringImpl(enum))
   }
   implicit def enumValueWrites[V <: E] : Writes[V] = new Writes[V] {
      def writes(value : V) : JsValue = JsString(asStringImpl(value))
   }
}

/**
 * This trait provides an instance of `Reads` for an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends FromJson[Color]
 * }}}
 * If you need your `Reads` instance to be case-insensitive you can override
 * the `caseSensitive` member like so:
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends FromJson[Color] {
 *   override val caseSensitive = false
 * }
 * }}}
 */
trait FromJson[E] { self : EnumApi[E] =>
   /** Indicates whether `reads` is case sensitive. */
   protected val caseSensitive = true
   implicit lazy val reads : Reads[E] =
      if (caseSensitive)
         new Reads[E] {
            def reads(js : JsValue) : JsResult[E] =
               js.asOpt[String]
                  .flatMap(fromStringImpl)
                  .fold[JsResult[E]](JsError(s"Expected $className but found: $js"))(JsSuccess(_))
         }
      else
         new Reads[E] {
            def reads(js : JsValue) : JsResult[E] = {
               val in = js.asOpt[String].map(_.toLowerCase)
               valuesImpl.map(v => asStringImpl(v).toLowerCase -> v)
                  .find(v => in.exists(_ == v._1))
                  .fold[JsResult[E]](JsError(s"Expected $className but found: $js"))(e => JsSuccess(e._2))
            }
         }

}

/**
 * This trait provides an instance of `Reads` for an enumeration. Unlike
 * `FromJson`, the `Reads` instance created by this trait operates on
 * `JsNumber` using the `Int` representation of the enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends FromJsonNumeric[Color]
 * }}}
 * Allows you to read JSON numbers as Color like so:
 * {{{
 * JsNumber(0).validate[Color] // JsResult(Red)
 * }}}
 */
trait FromJsonNumeric[E] { self : EnumApi[E] =>
   implicit lazy val reads : Reads[E] =
      new Reads[E] {
         def reads(js : JsValue) : JsResult[E] =
            js.asOpt[Int]
               .flatMap(fromIntImpl)
               .fold[JsResult[E]](JsError(s"Expected $className but found: $js"))(JsSuccess(_))
      }

}

/**
 * This trait provides an instance of `Format` for an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends JsonConverters[Color]
 * }}}
 * If you need your `Format` instance to be case-insensitive you can override
 * the `caseSensitive` member like so:
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends JsonConverters[Color] {
 *   override val caseSensitive = false
 * }
 * }}}
 */
trait JsonConverters[E] extends AsJson[E] with FromJson[E] { self : EnumApi[E] => }

/**
 * This trait provides an instance of `QueryStringBindable` for an enumeration.
 * It works by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends QueryStringConverters[Color]
 * }}}
 * If you need your `QueryStringBindable` instance to be case-insensitive you can override
 * the `caseSensitive` member like so:
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends QueryStringConverters[Color] {
 *   override val caseSensitive = false
 * }
 * }}}
 */
trait QueryStringConverters[E] { self : EnumApi[E] =>
   /** Indicates whether `bind` is case sensitive. */
   protected val caseSensitive = true
   implicit lazy val queryStringBindable : QueryStringBindable[E] =
      if (caseSensitive)
         new QueryStringBindable.Parsing(
            s => fromStringImpl(s).getOrElse(throwException(s)),
            asStringImpl(_),
            (key, e) => s"""Expected $className but found "${e.getMessage}" for key "$key".""")
      else
         new QueryStringBindable.Parsing(
            s => valuesImpl.map(v => asStringImpl(v).toLowerCase -> v)
               .find(v => s.toLowerCase == v._1)
               .fold[E](throwException(s))(_._2),
            asStringImpl(_),
            (key, e) => s"""Expected $className but found "${e.getMessage}" for key "$key".""")
}

/**
 * This trait provides an instance of `QueryStringBindable` for an enumeration.
 * Unlike `QueryStringConverters`, the `QueryStringBindable` instance created by
 * this trait treats the input as a number using the `Int` representation of the
 * enumeration. It works by extending the companion object of the enumeration class.
 * {{{
 * @enum class Digit {
 *   Zero
 *   One
 *   Two
 *   Three
 *   Four
 *   Five
 *   Six
 *   Seven
 *   Eight
 *   Nine
 * }
 * object Digit extends QueryStringNumericConverters[Digit]
 * }}}
 */
trait QueryStringNumericConverters[E] { self : EnumApi[E] =>
   implicit lazy val queryStringBindable : QueryStringBindable[E] =
      new QueryStringBindable.Parsing(
         s => Try(fromIntImpl(s.toInt)).toOption.flatten.getOrElse(throwException(s)),
         asIntImpl(_).toString,
         (key, e) => s"""Expected $className but found "${e.getMessage}" for key "$key".""")
}

/**
 * This trait provides an instance of `PathBindable` for an enumeration.
 * It works by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends PathBindable[Color]
 * }}}
 * If you need your `PathBindable` instance to be case-insensitive you can override
 * the `caseSensitive` member like so:
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends PathConverters[Color] {
 *   override val caseSensitive = false
 * }
 * }}}
 */
trait PathConverters[E] { self : EnumApi[E] =>
   /** Indicates whether `bind` is case sensitive. */
   protected val caseSensitive = true
   implicit lazy val pathBindable : PathBindable[E] =
      if (caseSensitive)
         new PathBindable.Parsing(
            s => fromStringImpl(s).getOrElse(throwException(s)),
            asStringImpl(_),
            (key, e) => s"""Expected $className but found "${e.getMessage}" for key "$key".""")
      else
         new PathBindable.Parsing(
            s => valuesImpl.map(v => asStringImpl(v).toLowerCase -> v)
               .find(v => s.toLowerCase == v._1)
               .fold[E](throwException(s))(_._2),
            asStringImpl(_),
            (key, e) => s"""Expected $className but found "${e.getMessage}" for key "$key".""")
}

/**
 * This trait provides an instance of `PathBindable` for an enumeration.
 * Unlike `PathConverters`, the `PathBindable` instance created by
 * this trait treats the input as a number using the `Int` representation of the
 * enumeration. It works by extending the companion object of the enumeration class.
 * {{{
 * @enum class Digit {
 *   Zero
 *   One
 *   Two
 *   Three
 *   Four
 *   Five
 *   Six
 *   Seven
 *   Eight
 *   Nine
 * }
 * object Digit extends PathNumericConverters[Digit]
 * }}}
 */
trait PathNumericConverters[E] { self : EnumApi[E] =>
   implicit lazy val queryStringBindable : PathBindable[E] =
      new PathBindable.Parsing(
         s => Try(fromIntImpl(s.toInt)).toOption.flatten.getOrElse(throwException(s)),
         asIntImpl(_).toString,
         (key, e) => s"""Expected $className but found "${e.getMessage}" for key "$key".""")
}
