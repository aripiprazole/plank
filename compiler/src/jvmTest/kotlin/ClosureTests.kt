package com.gabrielleeg1.plank.compiler

import org.junit.jupiter.api.Test

class ClosureTests {
  @Test
  fun `test declaring a closure with one level of nesting`() {
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
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test declaring a closure with two levels of nesting`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char): Void {
          fun nested(): Void {
            fun nested2(): Void {
              println("Hello, world! (nested 2nd level)");
            }

            println("Hello, world! (nested)");
          }

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
  fun `test getting closure with one level of nesting reference`() {
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
  fun `test getting closure with two levels of nesting reference`() {
    TestCompilation
      .of(
        """ module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char): Void {
          fun nested(): Void {
            fun nested2(): Void {
              println("Hello, world! (nested 2nd level)");
            }

            println("Hello, world! (nested)");
            nested2;
          }

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
  fun `test calling a closure function with two levels of nesting`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char): Void {
          fun nested(): Void {
            fun nested2(): Void {
              println("Hello world! (nested 2nd level)");
            }

            nested2();
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
  fun `test calling a closure function with two levels of nesting accessing a variable from the enclosing scope`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char): Void {
          fun nested(): Void {
            let x = "Example String";

            fun nested2(): Void {
              println(x);
            }

            println(x);
            nested2();
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

  @Test
  fun `test calling a closure function with two levels of nesting accessing a variable from the original function`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char): Void {
          let x = "Example String";
          fun nested(): Void {
            fun nested2(): Void {
              println(x);
            }

            println(x);
            nested2();
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

  @Test
  fun `test calling a closure function with one level of nesting`() {
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
  fun `test calling a closure function with one level of nesting accessing a variable from the enclosing scope`() {
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