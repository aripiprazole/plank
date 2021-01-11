package com.lorenzoog.jplank.compiler.utils

import com.lorenzoog.jplank.compiler.PlankContext

object FunctionUtils {
  fun generateName(name: String, context: PlankContext): String {
    return "${context.currentFile.moduleName}_$name"
  }

  fun generateName(name: String, module: String): String {
    return "${module}_$name"
  }
}
