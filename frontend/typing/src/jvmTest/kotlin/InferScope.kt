package org.plank.typing

import kotlin.test.assertEquals
import org.plank.syntax.element.Expr

class InferScope(builder: InferScope.() -> Unit = {}) {
  private var variables: Map<String, Type> = mapOf()

  init {
    builder()
  }

  fun variables(builder: MutableMap<String, Hole<Type>>.() -> Unit) {
    variables = buildMap(builder).mapValues { it.value.unwrap() }
  }

  fun runInfer(expr: Expr): Type {
    val engine = Infer().apply {
      variables.forEach { (name, type) ->
        currentScope.declareVariable(name, type)
      }
    }

    return engine.runInfer(expr).unwrap()
  }

  fun Type.toEqual(expected: Hole<Type>) {
    assertEquals(expected.unwrap(), this)
  }

  fun Type.toBe(expected: Type) {
    TODO("unify")
  }
}
