package org.plank.typing

class Scope {
  private val variables = mutableMapOf<String, Type>()

  fun findVariable(text: String): Type? {
    return variables[text]
  }

  fun declareVariable(text: String, type: Type) {
    variables[text] = type
  }
}
