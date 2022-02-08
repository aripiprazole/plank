package org.plank.codegen

import kotlin.test.Test

class IfTests {
  @Test
  fun `test if true then print x else print y`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char) -> Void {
          if (true) println("x") else println("y");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test if false then print x else print y`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char) -> Void {
          if (false) println("x") else println("y");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test if true then print x`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char) -> Void {
          if (true) println("x");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test if false then print x`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char) -> Void {
          if (false) println("x");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test print if true then x else y`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char) -> Void {
          println(if (true) "x" else "y");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test print if false then x else y`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char) -> Void {
          println(if (false) "x" else "y");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
