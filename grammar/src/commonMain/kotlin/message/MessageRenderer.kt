package com.lorenzoog.plank.grammar.message

import com.lorenzoog.plank.grammar.element.Location
import pw.binom.io.Closeable

interface MessageRenderer : Closeable {
  fun severe(message: String, location: Location? = null)
  fun warning(message: String, location: Location? = null)
  fun info(message: String)
}
