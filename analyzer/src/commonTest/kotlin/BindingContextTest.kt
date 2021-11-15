package com.gabrielleeg1.plank.analyzer.test

import com.gabrielleeg1.plank.analyzer.PlankType.Companion.int
import kotlin.test.Test

class BindingContextTest {
  @Test
  fun `test should evaluate an int32 constant`() {
    val const = bindContext("1") {
      visitConstExpr(constExpr())
    }

    assertNotViolated(const)
    assertType(const, int())
  }
}
