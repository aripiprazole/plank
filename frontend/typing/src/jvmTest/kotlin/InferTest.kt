package org.plank.typing

import org.junit.jupiter.api.Test
import org.plank.syntax.element.AccessExpr
import org.plank.syntax.element.CallExpr
import org.plank.syntax.element.ConstExpr

class InferTest {
  @Test
  fun `test simple application`() {
    val expr = CallExpr(AccessExpr("println"), ConstExpr("hello world"))

    InferScope {
      variables {
        put("println", Type.String arrow Type.Unit)
      }

      runInfer(expr).toEqual(Type.Unit)
    }
  }

  @Test
  fun `test generic application`() {
    val expr = CallExpr(AccessExpr("println"), ConstExpr("hello world"))

    InferScope {
      variables {
        put("println", Type.variable("a") arrow Type.variable("a"))
      }

      runInfer(expr).toEqual(Type.String)
    }
  }
}
