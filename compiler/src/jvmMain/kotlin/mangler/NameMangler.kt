package com.lorenzoog.plank.compiler.mangler

import com.lorenzoog.plank.compiler.PlankContext
import com.lorenzoog.plank.grammar.element.Decl

class NameMangler {
  fun mangle(context: PlankContext, function: Decl.FunDecl): String {
    if (function.isNative) {
      // todo add export tag to avoid it
      return buildString {
        append(function.location.file.module)
        append("_")
        append(function.name.text)
      }
    }

    return buildString {
      append(context.moduleName.hashCode())
      append(context.moduleName)
      append("::")
      append(function.name.text)
      append(function.name.hashCode())
    }
  }
}
