package macrame

import scala.math.Ordering

/**
 * The API exposed by enumerations created with the @enum macro.
 * Unlike Scala's Enumeration interface, all auto-generated functions
 * are protected so the user can choose which functions to expose on
 * their type.
 *
 * @tparam E The type of the enumeration.
 */
trait EnumApi[E] {
   /** Returns a string representation of the enum. */
   protected def asStringImpl(enum : E) : String

   /** Returns the case whose String representation matches, if there is one. */
   protected def fromStringImpl(str : String) : Option[E]

   /**
    * Returns a Short representation of the enum.
    * The first listed case is 1, the second 2, and so on.
    */
   protected def asShortImpl(enum : E) : Short = asIntImpl(enum).toShort

   /** Returns the case whose Short representation matches, if there is one. */
   protected def fromShortImpl(short : Short) : Option[E] = fromIntImpl(short.toInt)

   /**
    * Returns an Int representation of the enum.
    * The first listed case is 1, the second 2, and so on.
    */
   protected def asIntImpl(enum : E) : Int

   /** Returns the case whose Int representation matches, if there is one. */
   protected def fromIntImpl(int : Int) : Option[E]

   /**
    * Returns a Long representation of the enum.
    * The first listed case is 1, the second 2, and so on.
    */
   protected def asLongImpl(enum : E) : Long = asIntImpl(enum).toLong

   /** Returns the case whose Long representation matches, if there is one. */
   protected def fromLongImpl(long : Long) : Option[E] = fromIntImpl(long.toInt)

   /** Returns the case defined just after the given enum, if one exists. */
   protected def nextImpl(enum : E) : Option[E] =
      fromIntImpl(asIntImpl(enum) + 1)

   /** As `nextImpl` but modular: the last defined case wraps around to the first one. */
   protected def nextModImpl(enum : E) : E =
      nextImpl(enum) getOrElse firstImpl

   /** Returns the case defined just before the given enum, if one exists. */
   protected def prevImpl(enum : E) : Option[E] =
      fromIntImpl(asIntImpl(enum) - 1)

   /** As `prevImpl` but modular: the first defined case wraps around to the last one. */
   protected def prevModImpl(enum : E) : E =
      prevImpl(enum) getOrElse lastImpl

   /** The first case defined in the enum. */
   protected def firstImpl : E

   /** The last case defined in the enum. */
   protected def lastImpl : E

   /** An instance of `Ordering` based on the definition order of the cases. */
   protected def orderingImpl : Ordering[E] = Ordering.by(asShortImpl)

   /** The full set of enumeration values. */
   protected def valuesImpl : Set[E]

   /**
    * Provides the class name of the enumeration.
    * This is useful for traits that build on top of `EnumApi`.
    */
   protected def className : String
}
