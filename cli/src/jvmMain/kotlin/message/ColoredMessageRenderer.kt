package com.lorenzoog.jplank.message

import com.lorenzoog.jplank.element.Location
import com.lorenzoog.jplank.utils.blue
import com.lorenzoog.jplank.utils.red
import com.lorenzoog.jplank.utils.yellow
import java.io.PrintWriter

class ColoredMessageRenderer(
  private val flush: Boolean = false,
  private val writer: PrintWriter = PrintWriter(System.out),
  private val errWriter: PrintWriter = PrintWriter(System.err)
) : MessageRenderer {
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

  fun flush() {
    writer.flush()
    writer.close()
  }

  override fun close() {
    errWriter.flush()
    errWriter.close()
  }
}
