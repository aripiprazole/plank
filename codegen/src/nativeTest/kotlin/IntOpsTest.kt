package org.plank.codegen

import kotlin.test.Test

class IntOpsTest {
  @Test
  fun `test int eq operation`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) {
          print_bool(1 == 1);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test int neq operation`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) {
          print_bool(1 != 1);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test int gte operation`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) {
          print_bool(1 >= 1);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test int gt operation`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) {
          print_bool(1 > 1);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test int lte operation`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) {
          print_bool(1 <= 1);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test int lt operation`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) {
          print_bool(1 < 1);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test int add operation`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) {
          print_int(1 + 1);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test int sub operation`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) {
          print_int(1 - 1);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test int mul operation`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) {
          print_int(3 * 2);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test div mul operation`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) {
          print_int(6 / 2);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test fibonacci`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun fib(n: Int32) -> Int32 {
          if (n == 0) 0
          else if (n == 1) 1
          else fib(n - 1) + fib(n - 2)
        }

        fun main(argc: Int32, argv: **Char) {
          print_int(fib(0));
          print_int(fib(1));
          print_int(fib(2));
          print_int(fib(3));
          print_int(fib(4));
          print_int(fib(5));
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
