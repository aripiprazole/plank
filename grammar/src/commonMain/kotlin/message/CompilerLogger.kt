package com.gabrielleeg1.plank.grammar.message

import com.gabrielleeg1.plank.grammar.element.Location
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal

enum class LogLevel(val prefix: String, val color: TextColors) {
  Debug("debug", TextColors.brightRed),
  Verbose("verbose", TextColors.brightRed),
  Severe("severe", TextColors.red),
  Warning("warning", TextColors.brightYellow),
  Info("info", TextColors.brightWhite),
}

interface CompilerLogger {
  val terminal: Terminal

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
