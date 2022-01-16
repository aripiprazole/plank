package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl

fun CompilerContext.mangleFunction(function: ResolvedFunDecl): String {
  function.attribute("external")?.takeIf { it.arguments.isNotEmpty() }?.let {
    return it.argument(0) ?: error("TODO handle mangle errors")
  }

  return buildString {
    append(contextName)
    append(".")
    append(function.name.text)
  }
}
