package com.lorenzoog.plank.grammar.message

import com.lorenzoog.plank.grammar.element.Location

class SimpleCompilerLogger : CompilerLogger {
  override fun debug(message: String, location: Location?) {
    println("D: $location: $message")
  }

  override fun verbose(message: String, location: Location?) {
    println("V: $location: $message")
  }

  override fun severe(message: String, location: Location?) {
    println("E: $location: $message")
  }

  override fun warning(message: String, location: Location?) {
    println("W: $location: $message")
  }

  override fun info(message: String) {
    println("INFO: $message")
  }

  override fun close() {
    // nothing to do
  }
}
