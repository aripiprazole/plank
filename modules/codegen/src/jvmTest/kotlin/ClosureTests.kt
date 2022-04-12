@file:Suppress("MaximumLineLength", "MaxLineLength")

package org.plank.codegen

import kotlin.test.Test

class ClosureTests {
  @Test
  fun `test declaring a closure with one level of nesting`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          fun nested() -> () {
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

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          fun nested() -> () {
            fun nested2() -> () {
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

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          fun nested() -> () {
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

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          fun nested() -> () {
            fun nested2() -> () {
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

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          fun nested() -> () {
            fun nested2() -> () {
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

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          fun nested() -> () {
            let x = "Example String";

            fun nested2() -> () {
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

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          let x = "Example String";
          fun nested() -> () {
            fun nested2() -> () {
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

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          fun nested() -> () {
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

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          let x = "Example String";
          fun nested() -> () {
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

  @Test
  fun `test calling a parameterized closure function with one level of nesting`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          fun nested(x: *Char) -> () {
            println(x);
          }
          nested("Hello, world");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test calling a two parameterized closure function with one level of nesting`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          fun nested(x: *Char, y: *Char) -> () {
          }
          nested("Hello")("world");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test calling a two parameterized closure function with one level of nesting accessing a variable from the enclosing scope`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          let outside = "Example String";
          fun nested(x: *Char, y: *Char) -> () {
            println(outside);
            print(x);
            print(" ");
            println(y);
          }
          nested("Hello", "world");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
