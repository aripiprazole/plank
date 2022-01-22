package com.gabrielleeg1.plank.grammar.message

import com.gabrielleeg1.plank.grammar.element.Location

interface CompilerLogger {
  fun debug(message: String = "", location: Location? = null)
  fun verbose(message: String = "", location: Location? = null)
  fun severe(message: String = "", location: Location? = null)
  fun warning(message: String = "", location: Location? = null)
  fun info(message: String)
}
