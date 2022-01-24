package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl

fun CompilerContext.mangleFunction(function: ResolvedFunDecl, isNative: Boolean = false): String {
  function.attribute("external")?.takeIf { it.arguments.isNotEmpty() }?.let {
    if (isNative) {
      return it.argument(0) ?: error("TODO handle mangle errors")
    }
  }

  return buildString {
    append(name)
    append(".")
    append(function.name.text)
  }
}
