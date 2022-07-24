package org.plank.codegen

import kotlin.test.Test

class HelloWorldTests {
  @Test
  fun `test hello world`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          println("Hello, world!");
        }
        """.trimIndent(),
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test hello world call 2 times`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          println("Hello, world!");
          println("Hello, world!");
        }
        """.trimIndent(),
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
