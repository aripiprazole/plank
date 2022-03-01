package org.plank.codegen

import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.codegen.scope.CodegenCtx
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.QualifiedPath
import org.plank.syntax.element.toIdentifier

class MangledId(private val fn: CodegenCtx.() -> String) {
  operator fun plus(other: MangledId): MangledId = MangledId { get() + other.get() }
  operator fun plus(other: String): MangledId = MangledId { get() + other }

  fun CodegenCtx.get(): String = fn()
}

fun CodegenCtx.funMangled(decl: ResolvedFunDecl): MangledId = pathMangled {
  subst.types.toIdentifier() + decl.name
}

fun CodegenCtx.stringMangled(fn: CodegenCtx.() -> String): MangledId {
  val module = QualifiedPath(scope)

  return MangledId {
    buildString {
      append("_Z")
      module.fullPath.reversed().forEach { (name) ->
        append(name.length)
        append(name)
      }
      fn().also { str ->
        append(str.length)
        append(str)
      }
    }
  }
}

fun CodegenCtx.pathMangled(fn: CodegenCtx.() -> List<Identifier>): MangledId {
  val module = QualifiedPath(scope)

  return MangledId {
    buildString {
      append("_Z")
      module.fullPath.reversed().forEach { (name) ->
        append(name.length)
        append(name)
      }
      fn().reversed().forEach { (name) ->
        append(name.length)
        append(name)
      }
    }
  }
}

fun CodegenCtx.idMangled(fn: CodegenCtx.() -> Identifier): MangledId {
  return pathMangled { listOf(fn()) }
}
