package com.lorenzoog.jplank.message

import com.lorenzoog.jplank.element.Location
import pw.binom.io.Closeable

interface MessageRenderer : Closeable {
  fun severe(message: String, location: Location? = null)
  fun warning(message: String, location: Location? = null)
  fun info(message: String)
}
