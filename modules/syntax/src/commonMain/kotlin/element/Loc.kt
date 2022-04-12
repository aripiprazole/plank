package org.plank.syntax.element

import org.antlr.v4.kotlinruntime.Token
import org.plank.syntax.debug.DontDump
import org.plank.syntax.message.lineSeparator

sealed interface Loc {
  @DontDump
  val file: PlankFile

  fun endIn(loc: Loc): Loc
}

object GeneratedLoc : Loc {
  @DontDump
  override val file: PlankFile
    get() = error("Should not get location of generated code")

  override fun endIn(loc: Loc): Loc = GeneratedLoc

  override fun toString(): String = "Generated"
}

data class TextRange internal constructor(val a: Int, val b: Int, override val file: PlankFile) :
  Loc {
  val start = Point(a)
  val end = Point(b)

  val lines = file.content.split(lineSeparator).filterIndexed { i, _ ->
    i in (start.line - 1) until end.line
  }

  override fun toString(): String {
    return "${file.path}:${start.line}:${start.column}"
  }

  override fun endIn(loc: Loc): Loc = when (loc) {
    is TextRange -> copy(b = loc.b)
    is GeneratedLoc -> this
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is TextRange) return false

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

fun Loc(token: Token, file: PlankFile): Loc {
  return TextRange(token.startIndex, token.stopIndex, file)
}

fun Loc(start: Int, end: Int, file: PlankFile): Loc {
  return TextRange(start, end, file)
}
