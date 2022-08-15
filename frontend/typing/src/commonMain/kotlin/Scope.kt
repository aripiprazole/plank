package org.plank.typing

class Scope {
  private val variables = mutableMapOf<String, Type>().apply {
    put("println", Type.String arrow Type.Unit)
  }

  fun findVariable(text: String): Type? {
    return variables[text]
  }
}
