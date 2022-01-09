package com.gabrielleeg1.plank.grammar.element

sealed interface QualifiedPath : PlankElement {
  interface Visitor<T> {
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Replace with pattern matching")
    fun visit(path: QualifiedPath) = when (path) {
      is QualifiedPathNil -> visitQualifiedPathNil(path)
      is QualifiedPathCons -> visitQualifiedPathCons(path)
    }

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

  fun toIdentifier(): Identifier {
    return Identifier(text)
  }

  companion object {
    fun nil(): QualifiedPath {
      return QualifiedPathNil
    }

    fun cons(
      value: Identifier,
      next: QualifiedPath,
      location: Location = Location.Generated,
    ): QualifiedPath {
      return QualifiedPathCons(value, next, location)
    }

    fun from(identifier: Identifier): QualifiedPath {
      return QualifiedPathCons(identifier, nil(), identifier.location)
    }

    fun from(stringPath: String): QualifiedPath {
      return stringPath.split(".").asReversed().fold(nil()) { acc, next ->
        cons(Identifier(next), acc)
      }
    }
  }
}

data class QualifiedPathCons(
  val value: Identifier,
  val next: QualifiedPath,
  override val location: Location
) : QualifiedPath {
  override fun toString(): String {
    return "QualifiedPath(${fullPath.joinToString("/") { it.text }})"
  }
}

object QualifiedPathNil : QualifiedPath {
  override val location = Location.Generated

  override fun toString(): String {
    return "QualifiedPath(${fullPath.joinToString("/") { it.text }})"
  }
}
