package org.plank.codegen

import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.QualifiedPath
import org.plank.syntax.element.toIdentifier

// TODO: mangle parameters types with name
fun CodegenContext.mangle(path: ResolvedFunDecl): String {
  return when {
    subst.toMap().isNotEmpty() -> {
      mangle(*subst.types.map { it.toString().toIdentifier() }.toTypedArray(), path.name)
    }
    else -> mangle(path.name)
  }
}

fun CodegenContext.mangle(path: String): String {
  return mangle(Identifier(path))
}

fun CodegenContext.mangle(vararg path: Identifier): String {
  return mangle(QualifiedPath(path.toList()))
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
