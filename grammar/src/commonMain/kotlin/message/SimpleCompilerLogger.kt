package com.gabrielleeg1.plank.grammar.message

import com.gabrielleeg1.plank.grammar.element.Location

class SimpleCompilerLogger : CompilerLogger {
  override fun log(level: LogLevel, message: String, location: Location?) {
    println("${level.prefix}: $message")
  }
}
