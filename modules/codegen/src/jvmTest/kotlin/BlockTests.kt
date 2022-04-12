package org.plank.codegen

import kotlin.test.Test

class BlockTests {
  @Test
  fun `test simple block`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          {
            println("Hello, world!");
          };
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test simple block referencing outside`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          let message = "Hello, world!";
          {
            println(message);
          };
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test simple block with returned`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          let string = {
            "Hello, world!"
          };

          println(string);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test simple block with returned referencing outside`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          let message = "Hello, world!";
          let string = {
            message
          };

          println(string);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
