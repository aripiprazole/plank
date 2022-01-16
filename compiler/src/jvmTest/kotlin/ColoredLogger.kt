package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.grammar.element.Location
import com.gabrielleeg1.plank.grammar.message.CompilerLogger
import org.fusesource.jansi.Ansi
import java.io.PrintStream

class ColoredLogger(
  private val verbose: Boolean = false,
  private val debug: Boolean = false,
  private val flush: Boolean = false,
  private val writer: PrintStream = System.out,
  private val errWriter: PrintStream = System.err,
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

fun String.black(): String {
  return Ansi.ansi().fg(Ansi.Color.BLACK).toString() + this + Ansi().reset()
}

fun String.green(): String {
  return Ansi.ansi().fg(Ansi.Color.GREEN).toString() + this + Ansi().reset()
}

fun String.red(): String {
  return Ansi.ansi().fg(Ansi.Color.RED).toString() + this + Ansi().reset()
}

fun String.yellow(): String {
  return Ansi.ansi().fg(Ansi.Color.YELLOW).toString() + this + Ansi().reset()
}

fun String.blue(): String {
  return Ansi.ansi().fg(Ansi.Color.BLUE).toString() + this + Ansi().reset()
}

fun String.cyan(): String {
  return Ansi.ansi().fg(Ansi.Color.CYAN).toString() + this + Ansi().reset()
}

fun String.white(): String {
  return Ansi.ansi().fg(Ansi.Color.WHITE).toString() + this + Ansi().reset()
}

fun String.bold(): String {
  return Ansi.ansi().bold().toString() + this + Ansi().reset()
}

fun String.italic(): String {
  return Ansi.ansi().a(Ansi.Attribute.ITALIC).toString() + this + Ansi().reset()
}

fun String.underline(): String {
  return Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString() + this + Ansi().reset()
}

fun String.strikethrough(): String {
  return Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString() + this + Ansi().reset()
}

fun String.reset(): String {
  return Ansi.ansi().a(Ansi.Attribute.RESET).toString() + this + Ansi().reset()
}
