package com.lorenzoog.plank.grammar.element

sealed class QualifiedPath : PlankElement {
  interface Visitor<T> {
    fun visit(path: QualifiedPath) = path.accept(this)

    fun visitQualifiedPathCons(path: QualifiedPathCons): T
    fun visitQualifiedPathNil(path: QualifiedPathNil): T
  }

  val text: String
    get() = fullPath.joinToString(".")

  val fullPath: List<Identifier>
    get() = mutableListOf<Identifier>().apply {
      tailrec fun dumpPath(path: QualifiedPath) {
        when (path) {
          is QualifiedPathCons -> {
            add(path.value)

            dumpPath(path.next)
          }
          is QualifiedPathNil -> {
          }
        }
      }

      dumpPath(this@QualifiedPath)
    }

  abstract fun <T> accept(visitor: Visitor<T>): T

  override fun toString(): String {
    return "QualifiedPath(${fullPath.joinToString("/") { it.text }})"
  }

  companion object {
    fun nil(): QualifiedPath {
      return QualifiedPathNil
    }

    fun cons(value: Identifier, next: QualifiedPath, location: Location): QualifiedPath {
      return QualifiedPathCons(value, next, location)
    }

    fun from(identifier: Identifier): QualifiedPath {
      return QualifiedPathCons(identifier, QualifiedPathNil, identifier.location)
    }
  }
}

data class QualifiedPathCons(
  val value: Identifier,
  val next: QualifiedPath,
  override val location: Location
) : QualifiedPath() {
  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitQualifiedPathCons(this)
  }
}

object QualifiedPathNil : QualifiedPath() {
  override val location = Location.undefined()

  override fun <T> accept(visitor: Visitor<T>): T {
    return visitor.visitQualifiedPathNil(this)
  }
}
