package com.lorenzoog.jplank.compiler.mangler

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.element.Decl

class SimpleMangler : Mangler {
  override fun mangle(context: PlankContext, function: Decl.FunDecl): String {
    if (function.isNative) {
      return buildString {
        append(function.location.file.module)
        append("_")
        append(function.name.text)
      }
    }

    return buildString {
      append(function.location.file.module)
      append("::")
      append(function.name.text)
      append("@")
      append(function.parameters.size)
    }
  }
}
