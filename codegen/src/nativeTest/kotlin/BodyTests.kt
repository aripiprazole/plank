package org.plank.codegen

import kotlin.test.Test

class BodyTests {
  @Test
  fun `test code body`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> Void {
          println("Hello, world!");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test code body with returned`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun get_string() -> *Char { "Hello, world!" }

        fun main(argc: Int32, argv: **Char) -> Void {
          println(get_string());
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test expr body`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun get_string() -> *Char = "Hello, world!"

        fun main(argc: Int32, argv: **Char) -> Void {
          println(get_string());
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
