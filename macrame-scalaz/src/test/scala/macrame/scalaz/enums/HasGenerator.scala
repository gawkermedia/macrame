package macrame.scalaz.enums

import macrame.EnumApi

import org.scalacheck.{ Arbitrary, Gen }

trait HasGenerator[Enum] { self : EnumApi[Enum] =>
   lazy val gen = Gen.oneOf[Enum](valuesImpl.toSeq)
   implicit lazy val arbitrary : Arbitrary[Enum] = Arbitrary[Enum](gen)
}
