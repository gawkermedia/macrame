# Macramé-Play
Macrame-Play provides `Reads`, `Writes`,`Format`, `QueryStringBindable`, and `PathBindable` instances for Macramé enumerations.

## Getting Macramé-Play
This library requires Scala 2.13 and Java 17+ (Play 2.9 dropped support for Java 8).

If you're using SBT, add the following to your build file.
```scala
libraryDependencies += "com.kinja" %% "macrame-play" % "2.0.0-play-2.9.x"
```

## API Documentation
Full API documentation is available [here](http://claireneveu.github.io/macrame/doc/macrame-play/1.1.0-play-2.5.x/#package).

### Usage
This example shows how to make a `Format` instance for a `Color` enumeration:
```scala
import macrame.play.enums.JsonConverters

@enum class Color {
   Red
   Blue
   Yellow
}
object Color extends JsonConverters[Color]
```
