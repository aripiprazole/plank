package com.lorenzoog.plank.grammar.element

import org.antlr.v4.kotlinruntime.Token
import pw.binom.io.file.File

sealed class Location {
  abstract val file: PlankFile

  data class Defined internal constructor(
    val start: Int,
    val end: Int,
    override val file: PlankFile,
  ) : Location() {
    val line = file.content.take(start).count { it == File.SEPARATOR } + 1
    val column = file.content.take(start).count()

    override fun toString(): String {
      return "${file.path} ($line, $column)"
    }
  }

  object Generated : Location() {
    override val file: PlankFile get() = error("Should not get location of generated code")
  }

  companion object {
    fun of(token: Token, file: PlankFile): Location {
      return Defined(token.startIndex, token.stopIndex, file)
    }

    fun of(start: Int, end: Int, file: PlankFile): Location {
      return Defined(start, end, file)
    }

    fun undefined(): Location {
      return Generated
    }
  }
}
