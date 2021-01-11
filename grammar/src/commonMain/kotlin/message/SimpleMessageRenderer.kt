package com.lorenzoog.jplank.message

import com.lorenzoog.jplank.element.Location

class SimpleMessageRenderer : MessageRenderer {
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
