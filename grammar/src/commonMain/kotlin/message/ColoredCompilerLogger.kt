package com.gabrielleeg1.plank.grammar.message

import com.gabrielleeg1.plank.grammar.element.Location
import com.github.ajalt.mordant.rendering.TextColors.brightWhite
import com.github.ajalt.mordant.rendering.TextStyles.bold

expect val lineSeparator: String

class ColoredCompilerLogger(val debug: Boolean, val verbose: Boolean) : CompilerLogger {
  override fun log(level: LogLevel, message: String, location: Location?) {
    message.split(lineSeparator).forEach {
      println(bold(level.color(level.prefix) + brightWhite(": $it")))
    }
  }

  override fun debug(message: String, location: Location?) {
    if (debug) super.debug(message, location)
  }

  override fun verbose(message: String, location: Location?) {
    if (verbose) super.verbose(message, location)
  }
}
