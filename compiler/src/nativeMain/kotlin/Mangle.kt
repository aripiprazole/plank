package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.grammar.element.QualifiedPath

fun CodegenContext.mangleFunction(function: ResolvedFunDecl): String {
  val module = QualifiedPath(scope)

  return buildString {
    append("_Z")
    module.fullPath.reversed().forEach { (name) ->
      append(name.length)
      append(name)
    }
    append(function.name.text.length)
    append(function.name.text)
  }
}
