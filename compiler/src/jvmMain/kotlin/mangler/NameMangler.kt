package com.gabrielleeg1.plank.compiler.mangler

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.grammar.element.Decl

class NameMangler {
  fun mangle(context: CompilerContext, function: Decl.FunDecl): String {
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
