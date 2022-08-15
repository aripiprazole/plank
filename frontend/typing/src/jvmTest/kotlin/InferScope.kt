package org.plank.typing

import kotlin.test.assertEquals
import org.plank.syntax.element.Expr

class InferScope(builder: InferScope.() -> Unit = {}) {
  private var variables: Map<String, Type> = mapOf()

  init {
    builder()
  }

  fun variables(builder: MutableMap<String, Type>.() -> Unit) {
    variables = buildMap(builder)
  }

  fun runInfer(expr: Expr): Type {
    val engine = Infer().apply {
      variables.forEach { (name, type) ->
        currentScope.declareVariable(name, type)
      }
    }

    return engine.runInfer(expr)
  }

  fun Type.toEqual(expected: Type) {
    assertEquals(expected, this)
  }

  fun Type.toBe(expected: Type) {
    TODO("unify")
  }
}
