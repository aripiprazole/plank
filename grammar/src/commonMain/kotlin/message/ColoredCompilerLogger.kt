package com.gabrielleeg1.plank.grammar.message

import com.gabrielleeg1.plank.grammar.element.Location
import com.github.ajalt.mordant.rendering.TextColors.brightBlue
import com.github.ajalt.mordant.rendering.TextColors.brightGreen
import com.github.ajalt.mordant.rendering.TextColors.brightRed
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.rendering.TextColors.yellow

class ColoredCompilerLogger(val debug: Boolean, val verbose: Boolean) : CompilerLogger {
  override fun debug(message: String, location: Location?) {
    if (!debug) return

    when (location) {
      null -> println(brightGreen("D: $message"))
      else -> println(brightGreen("D: $location: $message"))
    }
  }

  override fun verbose(message: String, location: Location?) {
    if (!verbose) return

    when (location) {
      null -> println(brightRed("V: $message"))
      else -> println(brightRed("V: $location: $message"))
    }
  }

  override fun severe(message: String, location: Location?) {
    when (location) {
      null -> println(red("E: $message"))
      else -> println(red("E: $location: $message"))
    }
  }

  override fun warning(message: String, location: Location?) {
    when (location) {
      null -> println(yellow("W: $message"))
      else -> println(yellow("W: $location: $message"))
    }
  }

  override fun info(message: String) {
    println(brightBlue("I: $message"))
  }
}
