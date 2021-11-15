package com.gabrielleeg1.plank.cli.message

import com.gabrielleeg1.plank.cli.utils.blue
import com.gabrielleeg1.plank.cli.utils.green
import com.gabrielleeg1.plank.cli.utils.red
import com.gabrielleeg1.plank.cli.utils.yellow
import com.gabrielleeg1.plank.grammar.element.Location
import com.gabrielleeg1.plank.grammar.message.CompilerLogger
import java.io.PrintWriter

class ColoredLogger(
  private val verbose: Boolean = false,
  private val debug: Boolean = false,
  private val flush: Boolean = false,
  private val writer: PrintWriter = PrintWriter(System.out),
  private val errWriter: PrintWriter = PrintWriter(System.err)
) : CompilerLogger {
  override fun debug(message: String, location: Location?) {
    if (!verbose) {
      return
    }

    if (location == null) {
      writer.println("D: $message".green())
    } else {
      writer.println("D: $location: $message".green())
    }

    if (flush) writer.flush()
  }

  override fun verbose(message: String, location: Location?) {
    if (!verbose) {
      return
    }

    if (location == null) {
      writer.println("V: $message".green())
    } else {
      writer.println("V: $location: $message".green())
    }

    if (flush) writer.flush()
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
