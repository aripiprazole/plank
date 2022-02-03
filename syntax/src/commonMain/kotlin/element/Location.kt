package org.plank.syntax.element

import org.antlr.v4.kotlinruntime.Token
import org.plank.syntax.debug.DontDump
import org.plank.syntax.message.lineSeparator

sealed interface Location {
  @DontDump
  val file: PlankFile

  data class Range internal constructor(val a: Int, val b: Int, override val file: PlankFile) :
    Location {
    val start = Point(a)
    val end = Point(b)

    val lines = file.content.split(lineSeparator).filterIndexed { i, _ ->
      i in (start.line - 1) until end.line
    }

    override fun toString(): String {
      return "${file.path}:${start.line}:${start.column}"
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Range) return false

      if (a != other.a) return false
      if (b != other.b) return false
      if (file != other.file) return false

      return true
    }

    override fun hashCode(): Int {
      var result = a
      result = 31 * result + b
      result = 31 * result + file.hashCode()
      result = 31 * result + start.hashCode()
      result = 31 * result + end.hashCode()
      result = 31 * result + lines.hashCode()
      return result
    }

    @Suppress("ConvertSecondaryConstructorToPrimary")
    inner class Point {
      val line: Int
      val column: Int

      constructor(pos: Int) {
        var lineNumber = 0
        var charPos = 0
        for (line in file.content.split(lineSeparator)) {
          lineNumber++
          var columnNumber = 0
          for (column in line) {
            charPos++
            columnNumber++

            if (charPos == pos) {
              this.line = lineNumber
              this.column = columnNumber
              return
            }
          }
          charPos++
          if (charPos == pos) {
            this.line = lineNumber
            this.column = columnNumber
            return
          }
        }

        this.line = -1
        this.column = -1
      }
    }
  }

  object Generated : Location {
    @DontDump
    override val file: PlankFile
      get() = error("Should not get location of generated code")

    override fun toString(): String = "Generated"
  }
}

fun Location(token: Token, file: PlankFile): Location {
  return Location.Range(token.startIndex, token.stopIndex, file)
}

fun Location(start: Int, end: Int, file: PlankFile): Location {
  return Location.Range(start, end, file)
}
