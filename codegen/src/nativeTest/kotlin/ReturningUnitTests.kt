package org.plank.codegen

import kotlin.test.Test

class ReturningUnitTests {
  @Test
  fun `test return unit of ffi`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char): Void {
          return println("Hello, world!");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
