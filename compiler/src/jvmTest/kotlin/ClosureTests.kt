package com.gabrielleeg1.plank.compiler

import org.junit.jupiter.api.Test

class ClosureTests {
  @Test
  fun `test getting closure function reference`() {
    TestCompilation
      .of(
        """ module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char): Void {
          fun nested(): Void {
            println("Hello, world! (nested)");
          }

          println("Hello, world!");
          nested;
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test calling a closure function`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char): Void {
          fun nested(): Void {
            println("Hello, world! (nested)");
          }

          println("Hello, world!");
          nested();
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test closure accessing variable outside one level of the scope`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char): Void {
          let x = "Example String";
          fun nested(): Void {
            println(x);
          }
          nested();
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
