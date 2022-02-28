package org.plank.syntax.message

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
import org.plank.syntax.element.Loc
import org.plank.syntax.element.TextRange

enum class LogLevel(val prefix: String, val color: TextColors) {
  Debug("debug", brightRed),
  Verbose("verbose", brightRed),
  Severe("severe", red),
  Warning("warning", brightYellow),
  Info("info", brightWhite),
}

interface CompilerLogger {
  fun log(level: LogLevel, message: String, loc: Loc?)

  fun debug(message: String = "", loc: Loc? = null): Unit =
    log(LogLevel.Debug, message, loc)

  fun verbose(message: String = "", loc: Loc? = null): Unit =
    log(LogLevel.Verbose, message, loc)

  fun severe(message: String = "", loc: Loc? = null): Unit =
    log(LogLevel.Severe, message, loc)

  fun warning(message: String = "", loc: Loc? = null) =
    log(LogLevel.Warning, message, loc)

  fun info(message: String): Unit = log(LogLevel.Info, message, null)
}

fun CompilerLogger(debug: Boolean = false, verbose: Boolean = false): CompilerLogger {
  return CompilerLoggerImpl(debug, verbose)
}

expect val lineSeparator: String

object NoopCompilerLogger : CompilerLogger {
  override fun log(level: LogLevel, message: String, loc: Loc?) {
    return
  }
}

private class CompilerLoggerImpl(val debug: Boolean = false, val verbose: Boolean = false) :
  CompilerLogger {
  private val terminal = Terminal(AnsiLevel.TRUECOLOR, tabWidth = 1)

  override fun log(level: LogLevel, message: String, loc: Loc?) {
    message.split(lineSeparator).forEach { lineMessage ->
      terminal.println(bold(level.color(level.prefix) + brightWhite(": $lineMessage")))

      if (loc is TextRange) {
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
            val lines = loc.lines.filterNot { it.isBlank() || it.isEmpty() }
            val line = lines.first()

            row("", "-->", loc) { borders = Borders.NONE }
            row("", "|", "") { borders = Borders.NONE }
            row(loc.start.line, "|", line) {
              borders = Borders.NONE
            }

            if (lines.size > 1) {
              row("", "|", "") { borders = Borders.NONE }
            } else {
              val errorIndicator = MutableList(line.length) { " " }
              for (i in loc.start.column..loc.end.column) {
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

  override fun debug(message: String, loc: Loc?) {
    if (debug) super.debug(message, loc)
  }

  override fun verbose(message: String, loc: Loc?) {
    if (verbose) super.verbose(message, loc)
  }
}
