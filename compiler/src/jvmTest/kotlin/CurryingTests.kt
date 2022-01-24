package com.gabrielleeg1.plank.compiler

import org.junit.jupiter.api.Test

class CurryingTests {
  @Test
  fun `test basic currying`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char): Void {
          println("Hello, world");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test basic currying with empty parameters function`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun empty(): Void {
          println("Hello, world!");
        }

        fun main(argc: Int32, argv: **Char): Void {
          empty();
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
