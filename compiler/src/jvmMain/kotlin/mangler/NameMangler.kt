package com.gabrielleeg1.plank.compiler.mangler

import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.compiler.CompilerContext

class NameMangler {
  fun mangle(context: CompilerContext, function: ResolvedFunDecl): String {
    if (function.hasAttribute("native")) {
      // todo add export tag to avoid it
      return buildString {
        append(function.location.file.module.text)
        append("_")
        append(function.name.text)
      }
    }

    return buildString {
      append(context.moduleName)
      append(".")
      append(function.name.text)
    }
  }
}
