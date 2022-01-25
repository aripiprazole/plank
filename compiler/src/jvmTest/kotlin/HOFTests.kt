package com.gabrielleeg1.plank.compiler

import org.junit.jupiter.api.Test

class HOFTests {
  @Test
  fun `test passing hof`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun hof(f: *Char -> Void): Void {
          f("String (hof)");
        }

        fun main(argc: Int32, argv: **Char): Void {
          println("String (outside hof)");

          fun nested(value: *Char): Void {
            println(value);
          }

          hof(nested);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test passing hof closure`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun hof_nesting(f: *Char -> Void): Void {
          f("String (hof)");
        }

        fun main(argc: Int32, argv: **Char): Void {
          println("String (outside hof)");

          let x = "Example String";

          fun nested(value: *Char): Void {
            println(x);
            println(value);
          }

          hof_nesting(nested);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test passing hof closure with non closure`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun hof_nesting(f: *Char -> Void): Void {
          f("String (hof)");
        }

        fun main(argc: Int32, argv: **Char): Void {
          println("Hello");
          hof_nesting(println);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test passing hof closure with currying directly`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun hof(f: *Char -> Void): Void {
          f("String (hof)");
        }

        fun prefixed(prefix: *Char, message: *Char): Void {
          print(prefix);
          print(" => ");
          println(message);
        }

        fun main(argc: Int32, argv: **Char): Void {
          hof(prefixed("info"));
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test passing hof closure with currying by reference`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun hof(f: *Char -> Void): Void {
          f("String (hof)");
        }

        fun prefixed(prefix: *Char, message: *Char): Void {
          print(prefix);
          print(" => ");
          println(message);
        }

        fun main(argc: Int32, argv: **Char): Void {
          let f = prefixed("info");
          hof(f);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
