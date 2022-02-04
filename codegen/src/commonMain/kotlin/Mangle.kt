package org.plank.codegen

import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.QualifiedPath

fun CodegenContext.mangle(path: ResolvedFunDecl): String {
  return mangle(path.name)
}

fun CodegenContext.mangle(path: String): String {
  return mangle(Identifier(path))
}

fun CodegenContext.mangle(path: Identifier): String {
  return mangle(QualifiedPath(path))
}

fun CodegenContext.mangle(path: QualifiedPath): String {
  val module = QualifiedPath(scope)

  return buildString {
    append("_Z")
    module.fullPath.reversed().forEach { (name) ->
      append(name.length)
      append(name)
    }
    path.fullPath.reversed().forEach { (name) ->
      append(name.length)
      append(name)
    }
  }
}
