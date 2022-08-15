package org.plank.typing

import org.junit.jupiter.api.Test
import org.plank.syntax.element.AccessExpr
import org.plank.syntax.element.CallExpr
import org.plank.syntax.element.ConstExpr

class InferTest {
  @Test
  fun `test type system`() {
    val expr = CallExpr(AccessExpr("println"), ConstExpr("hello world"))

    println(Infer().runInfer(expr))
  }
}
