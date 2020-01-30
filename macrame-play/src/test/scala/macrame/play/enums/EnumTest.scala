package macrame.play.enums

import macrame.enum

import org.scalatest.FunSuite

import play.api.libs.json._
import play.api.mvc.{ PathBindable, QueryStringBindable }

class EnumTest extends FunSuite {

   val yellowStr = "YELLOW"

   test("AsJson should work.") {
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow(yellowStr)
      }
      object Color extends AsJson[Color]

      assert(Json.toJson(Color.Red : Color) == JsString("Red"))
      assert(Json.toJson(Color.Blue : Color) == JsString("BLUE"))
      assert(Json.toJson(Color.Yellow : Color) == JsString("YELLOW"))
   }

   test("FromJson should work.") {
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow(yellowStr)
      }
      object Color extends FromJson[Color]

      assert(JsString("Red").as[Color] == Color.Red)
      assert(JsString("BLUE").as[Color] == Color.Blue)
      assert(JsString("YELLOW").as[Color] == Color.Yellow)
   }

   test("FromJsonNumeric should work.") {
      @enum class Color {
         Red
         Blue
         Yellow
      }
      object Color extends FromJsonNumeric[Color]

      assert(JsNumber(0).as[Color] == Color.Red)
      assert(JsNumber(1).as[Color] == Color.Blue)
      assert(JsNumber(2).as[Color] == Color.Yellow)
   }

   test("JsonConverters should work.") {
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow(yellowStr)
      }
      object Color extends JsonConverters[Color]

      suppressUnusedWarning(Color)
      assertCompiles("""implicitly[Format[Color]]""")
   }

   test("Case insensitive FromJson should work.") {
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow("YeLlOw")
      }
      object Color extends FromJson[Color] {
         override val caseSensitive = false
      }

      assert(JsString("RED").as[Color] == Color.Red)
      assert(JsString("blue").as[Color] == Color.Blue)
      assert(JsString("YELLOW").as[Color] == Color.Yellow)
      assert(JsString("yellow").as[Color] == Color.Yellow)
   }

   test("QueryStringConverters should work.") {
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow("YeLlOw")
      }
      object Color extends QueryStringConverters[Color]

      val qsb = implicitly[QueryStringBindable[Color]]
      val params = Map(
         "a" -> Seq("Red"),
         "b" -> Seq("BLUE"),
         "c" -> Seq("YeLlOw"),
         "d" -> Seq("yellow"),
         "f" -> Seq("blue"),
         "g" -> Seq("red"))

      assert(qsb.bind("a", params) == Some(Right(Color.Red)))
      assert(qsb.bind("b", params) == Some(Right(Color.Blue)))
      assert(qsb.bind("c", params) == Some(Right(Color.Yellow)))
      assert(qsb.bind("d", params) == Some(Left("""Expected Color but found "yellow" for key "d".""")))
      assert(qsb.bind("f", params) == Some(Left("""Expected Color but found "blue" for key "f".""")))
      assert(qsb.bind("g", params) == Some(Left("""Expected Color but found "red" for key "g".""")))
   }

   test("PathConverters should work.") {
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow("YeLlOw")
      }
      object Color extends PathConverters[Color]

      val pb = implicitly[PathBindable[Color]]

      assert(pb.bind("a", "Red") == Right(Color.Red))
      assert(pb.bind("b", "BLUE") == Right(Color.Blue))
      assert(pb.bind("c", "YeLlOw") == Right(Color.Yellow))
      assert(pb.bind("d", "yellow") == Left("""Expected Color but found "yellow" for key "d"."""))
      assert(pb.bind("e", "blue") == Left("""Expected Color but found "blue" for key "e"."""))
      assert(pb.bind("f", "red") == Left("""Expected Color but found "red" for key "f"."""))
   }

   test("QueryStringNumericConverters should work.") {
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow("YeLlOw")
      }
      object Color extends QueryStringNumericConverters[Color]

      val qsb = implicitly[QueryStringBindable[Color]]
      val params = Map(
         "a" -> Seq("0"),
         "b" -> Seq("1"),
         "c" -> Seq("2"),
         "d" -> Seq("3"),
         "f" -> Seq("4"),
         "g" -> Seq("5"))

      assert(qsb.bind("a", params) == Some(Right(Color.Red)))
      assert(qsb.bind("b", params) == Some(Right(Color.Blue)))
      assert(qsb.bind("c", params) == Some(Right(Color.Yellow)))
      assert(qsb.bind("d", params) == Some(Left("""Expected Color but found "3" for key "d".""")))
      assert(qsb.bind("f", params) == Some(Left("""Expected Color but found "4" for key "f".""")))
      assert(qsb.bind("g", params) == Some(Left("""Expected Color but found "5" for key "g".""")))
   }

   test("PathNumericConverters should work.") {
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow("YeLlOw")
      }
      object Color extends PathNumericConverters[Color]

      val pb = implicitly[PathBindable[Color]]

      assert(pb.bind("a", "0") == Right(Color.Red))
      assert(pb.bind("b", "1") == Right(Color.Blue))
      assert(pb.bind("c", "2") == Right(Color.Yellow))
      assert(pb.bind("d", "3") == Left("""Expected Color but found "3" for key "d"."""))
      assert(pb.bind("e", "4") == Left("""Expected Color but found "4" for key "e"."""))
      assert(pb.bind("f", "5") == Left("""Expected Color but found "5" for key "f"."""))
   }

   test("Case insensitive QueryStringConverters should work.") {
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow("YeLlOw")
      }
      object Color extends QueryStringConverters[Color] {
         override val caseSensitive = false
      }

      val qsb = implicitly[QueryStringBindable[Color]]
      val params = Map(
         "a" -> Seq("Red"),
         "b" -> Seq("BLUE"),
         "c" -> Seq("YeLlOw"),
         "d" -> Seq("yellow"),
         "f" -> Seq("blue"),
         "g" -> Seq("RED"),
         "h" -> Seq("foo"))

      assert(qsb.bind("a", params) == Some(Right(Color.Red)))
      assert(qsb.bind("b", params) == Some(Right(Color.Blue)))
      assert(qsb.bind("c", params) == Some(Right(Color.Yellow)))
      assert(qsb.bind("d", params) == Some(Right(Color.Yellow)))
      assert(qsb.bind("f", params) == Some(Right(Color.Blue)))
      assert(qsb.bind("g", params) == Some(Right(Color.Red)))
      assert(qsb.bind("h", params) == Some(Left("""Expected Color but found "foo" for key "h".""")))
   }

   test("Case insensitive PathConverters should work.") {
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow("YeLlOw")
      }
      object Color extends PathConverters[Color] {
         override val caseSensitive = false
      }

      val pb = implicitly[PathBindable[Color]]

      assert(pb.bind("a", "Red") == Right(Color.Red))
      assert(pb.bind("b", "BLUE") == Right(Color.Blue))
      assert(pb.bind("c", "YeLlOw") == Right(Color.Yellow))
      assert(pb.bind("d", "yellow") == Right(Color.Yellow))
      assert(pb.bind("f", "blue") == Right(Color.Blue))
      assert(pb.bind("g", "RED") == Right(Color.Red))
      assert(pb.bind("h", "foo") == Left("""Expected Color but found "foo" for key "h"."""))
   }
}
