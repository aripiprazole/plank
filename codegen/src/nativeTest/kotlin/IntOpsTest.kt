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
}
