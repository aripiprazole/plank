package com.lorenzoog.plank.cli.message

import com.lorenzoog.plank.cli.utils.blue
import com.lorenzoog.plank.cli.utils.green
import com.lorenzoog.plank.cli.utils.red
import com.lorenzoog.plank.cli.utils.yellow
import com.lorenzoog.plank.grammar.element.Location
import com.lorenzoog.plank.grammar.message.CompilerLogger
import java.io.PrintWriter

class ColoredLogger(
  private val debug: Boolean = false,
  private val flush: Boolean = false,
  private val writer: PrintWriter = PrintWriter(System.out),
  private val errWriter: PrintWriter = PrintWriter(System.err)
) : CompilerLogger {
  override fun debug(message: String, location: Location?) {
    if (!debug) {
      return
    }

    if (location == null) {
      errWriter.println("E: $message".green())
    } else {
      errWriter.println("E: $location: $message".green())
    }

    if (flush) errWriter.flush()
  }

  override fun severe(message: String, location: Location?) {
    if (location == null) {
      errWriter.println("E: $message".red())
    } else {
      errWriter.println("E: $location: $message".red())
    }

    if (flush) errWriter.flush()
  }

  override fun warning(message: String, location: Location?) {
    if (location == null) {
      writer.println("W: $message".yellow())
    } else {
      writer.println("W: $location: $message".yellow())
    }

    if (flush) writer.flush()
  }

  override fun info(message: String) {
    writer.println("I: $message".blue())
    if (flush) writer.flush()
  }

  override fun close() {
    errWriter.flush()
    errWriter.close()
  }
}
