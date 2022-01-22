package com.gabrielleeg1.plank.grammar.message

import com.gabrielleeg1.plank.grammar.element.Location
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.rendering.BorderStyle
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextColors.brightWhite
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal

expect val lineSeparator: String

class SimpleCompilerLogger(val debug: Boolean = false, val verbose: Boolean = false) :
  CompilerLogger {
  override val terminal = Terminal(AnsiLevel.TRUECOLOR, tabWidth = 1)

  override fun log(level: LogLevel, message: String, location: Location?) {
    message.split(lineSeparator).forEach { lineMessage ->
      when (location) {
        is Location.Range -> {
          val dump = table {
            borderStyle = BorderStyle.BLANK

            column(0) {
              align = TextAlign.LEFT
              borders = Borders.LEFT
              style(TextColors.blue, bold = true)
            }

            column(1) {
              align = TextAlign.LEFT
              borders = Borders.NONE
              style(TextColors.blue, bold = true)
            }

            column(2) {
              align = TextAlign.LEFT
              borders = Borders.NONE
            }

            body {
              val line = location.lines.first()

              row("", "-->", location) { borders = Borders.NONE }
              row("", "|", "") { borders = Borders.NONE }
              row(location.start.line, "|", line) {
                borders = Borders.NONE
              }

              if (location.lines.size > 1) {
                row("", "|", "") { borders = Borders.NONE }
              } else {
                val errorIndicator = MutableList(line.length) { " " }
                for (i in location.start.column..location.end.column) {
                  errorIndicator[i] = "^"
                }

                row("", "|", bold(red(errorIndicator.joinToString("")))) {
                  borders = Borders.NONE
                }
                row("", "|", "") { borders = Borders.NONE }
              }
            }
          }

          terminal.println(dump)
        }
        else -> terminal.println(bold(level.color(level.prefix) + brightWhite(": $lineMessage")))
      }
    }
  }

  override fun debug(message: String, location: Location?) {
    if (debug) super.debug(message, location)
  }

  override fun verbose(message: String, location: Location?) {
    if (verbose) super.verbose(message, location)
  }
}
