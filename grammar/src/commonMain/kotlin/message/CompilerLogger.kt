package org.plank.grammar.message

import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.rendering.BorderStyle
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextColors.brightRed
import com.github.ajalt.mordant.rendering.TextColors.brightWhite
import com.github.ajalt.mordant.rendering.TextColors.brightYellow
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import org.plank.grammar.element.Location

enum class LogLevel(val prefix: String, val color: TextColors) {
  Debug("debug", brightRed),
  Verbose("verbose", brightRed),
  Severe("severe", red),
  Warning("warning", brightYellow),
  Info("info", brightWhite),
}

interface CompilerLogger {
  fun log(level: LogLevel, message: String, location: Location?)

  fun debug(message: String = "", location: Location? = null): Unit =
    log(LogLevel.Debug, message, location)

  fun verbose(message: String = "", location: Location? = null): Unit =
    log(LogLevel.Verbose, message, location)

  fun severe(message: String = "", location: Location? = null): Unit =
    log(LogLevel.Severe, message, location)

  fun warning(message: String = "", location: Location? = null) =
    log(LogLevel.Warning, message, location)

  fun info(message: String): Unit = log(LogLevel.Info, message, null)
}

expect val lineSeparator: String

class SimpleCompilerLogger(val debug: Boolean = false, val verbose: Boolean = false) :
  CompilerLogger {
  private val terminal = Terminal(AnsiLevel.TRUECOLOR, tabWidth = 1)

  override fun log(level: LogLevel, message: String, location: Location?) {
    message.split(lineSeparator).forEach { lineMessage ->
      terminal.println(bold(level.color(level.prefix) + brightWhite(": $lineMessage")))

      if (location is Location.Range) {
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
            val lines = location.lines.filterNot { it.isBlank() || it.isEmpty() }
            val line = lines.first()

            row("", "-->", location) { borders = Borders.NONE }
            row("", "|", "") { borders = Borders.NONE }
            row(location.start.line, "|", line) {
              borders = Borders.NONE
            }

            if (lines.size > 1) {
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
    }
  }

  override fun debug(message: String, location: Location?) {
    if (debug) super.debug(message, location)
  }

  override fun verbose(message: String, location: Location?) {
    if (verbose) super.verbose(message, location)
  }
}
