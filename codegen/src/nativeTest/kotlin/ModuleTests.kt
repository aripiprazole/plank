package org.plank.codegen

import kotlin.test.Test

class ModuleTests {
  @Test
  fun `test creating nested module`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        module Print {
          fun str(v: *Char) {
            println(v);
          }
        };

        fun main(argc: Int32, argv: **Char): Void {
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test calling function from nested module`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        module Print {
          fun str(v: *Char) {
            println(v);
          }
        };

        fun main(argc: Int32, argv: **Char): Void {
          Print.str("Hello world");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test calling function from nested module with nested import`() {
    TestCompilation
      .of(
        """
        module Main;

        module Print {
          import Std.IO;

          fun str(v: *Char) {
            println(v);
          }
        };

        fun main(argc: Int32, argv: **Char): Void {
          Print.str("Hello world");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
