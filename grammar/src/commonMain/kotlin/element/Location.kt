package com.gabrielleeg1.plank.grammar.element

import com.gabrielleeg1.plank.grammar.debug.DontDump
import com.gabrielleeg1.plank.grammar.message.lineSeparator
import org.antlr.v4.kotlinruntime.Token

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

    override fun toString(): String = "Location.Generated"
  }
}

fun Location(token: Token, file: PlankFile): Location {
  return Location.Range(token.startIndex, token.stopIndex, file)
}

fun Location(start: Int, end: Int, file: PlankFile): Location {
  return Location.Range(start, end, file)
}
