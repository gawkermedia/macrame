package macrame.scalaz.enums

import macrame.EnumApi

import org.scalacheck.{ Arbitrary, Gen }

trait HasGenerator[E] { self : EnumApi[E] =>
   lazy val gen = Gen.oneOf[E](valuesImpl.toSeq)
   implicit lazy val arbitrary : Arbitrary[E] = Arbitrary[E](gen)
}
