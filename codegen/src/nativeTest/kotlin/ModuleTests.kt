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

        fun main(argc: Int32, argv: **Char) -> Void {
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

        fun main(argc: Int32, argv: **Char) -> Void {
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

        fun main(argc: Int32, argv: **Char) -> Void {
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
  fun `test create module variable`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        module Test {
          let x = "hello, world";
        };

        fun main(argc: Int32, argv: **Char) -> Void {
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test create and use module variable`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        module Test {
          let x = "hello, world";
        };

        fun main(argc: Int32, argv: **Char) -> Void {
          println(Test.x);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test create and use module variable 2 times`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        module Test {
          let x = "hello, world";
        };

        fun main(argc: Int32, argv: **Char) -> Void {
          println(Test.x);
          println(Test.x);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test create and use module variable with function`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        module Test {
          fun buildString() -> *Char {
            return "hello, world";
          }

          let x = buildString();
        };

        fun main(argc: Int32, argv: **Char) -> Void {
          println(Test.x);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test create and call module variable`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        module Test {
          let x = println;
        };

        fun main(argc: Int32, argv: **Char) -> Void {
          Test.x("hello");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test create mutable module variable`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        module Test {
          let mutable x = "hello, world";
        };

        fun main(argc: Int32, argv: **Char) -> Void {
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test create and update mutable module variable`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        module Test {
          let mutable x = "hello, world";
        };

        fun main(argc: Int32, argv: **Char) -> Void {
          Test.x := "updated";
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test create update and use mutable module variable`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        module Test {
          let mutable x = "hello, world";
        };

        fun main(argc: Int32, argv: **Char) -> Void {
          println(Test.x);
          Test.x := "updated";
          println(Test.x);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
