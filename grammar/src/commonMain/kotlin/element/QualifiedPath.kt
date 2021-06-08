package com.lorenzoog.plank.grammar.element

sealed class QualifiedPath : PlankElement {
  interface Visitor<T> {
    fun visit(path: QualifiedPath) = path.accept(this)

    fun visitQualifiedPathCons(path: QualifiedPathCons): T
    fun visitQualifiedPathNil(path: QualifiedPathNil): T
  }

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
