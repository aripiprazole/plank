package com.gabrielleeg1.plank.grammar.element

import com.gabrielleeg1.plank.grammar.debug.DontDump
import org.antlr.v4.kotlinruntime.Token
import pw.binom.io.file.File

sealed interface Location {
  @DontDump
  val file: PlankFile

  data class Defined internal constructor(
    val start: Int,
    val end: Int,
    override val file: PlankFile,
  ) : Location {
    val line = file.content.take(start).count { it == File.SEPARATOR } + 1
    val column = file.content.take(start).count()

    override fun toString(): String {
      return "${file.path} ($line, $column)"
    }
  }

  object Generated : Location {
    @DontDump
    override val file: PlankFile get() = error("Should not get location of generated code")

    override fun toString(): String = "Location.Generated"
  }
}

fun Location(token: Token, file: PlankFile): Location {
  return Location.Defined(token.startIndex, token.stopIndex, file)
}

fun Location(start: Int, end: Int, file: PlankFile): Location {
  return Location.Defined(start, end, file)
}
