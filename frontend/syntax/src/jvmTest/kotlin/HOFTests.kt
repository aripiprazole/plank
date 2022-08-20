package org.plank.syntax

import kotlin.test.Test

class HOFTests {
  @Test
  fun `test passing hof`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun hof(f: *Char -> ()) -> () {
          f("String (hof)");
        }

        fun main(argc: Int32, argv: **Char) -> () {
          println("String (outside hof)");

          fun nested(value: *Char) -> () {
            println(value);
          }

          hof(nested);
        }
        """.trimIndent(),
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

        use Std.IO;

        fun hof_nesting(f: *Char -> ()) -> () {
          f("String (hof)");
        }

        fun main(argc: Int32, argv: **Char) -> () {
          println("String (outside hof)");

          let x = "Example String";

          fun nested(value: *Char) -> () {
            println(x);
            println(value);
          }

          hof_nesting(nested);
        }
        """.trimIndent(),
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

        use Std.IO;

        fun hof_nesting(f: *Char -> ()) -> () {
          f("String (hof)");
        }

        fun main(argc: Int32, argv: **Char) -> () {
          println("Hello");
          hof_nesting(println);
        }
        """.trimIndent(),
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

        use Std.IO;

        fun hof(f: *Char -> ()) -> () {
          f("String (hof)");
        }

        fun prefixed(prefix: *Char, message: *Char) -> () {
          print(prefix);
          print(" => ");
          println(message);
        }

        fun main(argc: Int32, argv: **Char) -> () {
          hof(prefixed("info"));
        }
        """.trimIndent(),
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test passing hof with many parameters`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun hof(write: *Char -> *Char -> ()) -> () {
          write("String", "(hof)");
          write("String2", "(hof)2");
        }

        fun main(argc: Int32, argv: **Char) -> () {
          fun write(message: *Char, prefix: *Char) -> () {
            print(prefix);
            print(" => ");
            println(message);
          }

          hof(write);
        }
        """.trimIndent(),
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test passing hof closure with many parameters with closure referencing outside`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun hof(f: *Char -> *Char -> ()) -> () {
          f("(hof)", "String");
        }

        fun main(argc: Int32, argv: **Char) -> () {
          let scope = "info";
          fun prefixed(prefix: *Char, message: *Char) -> () {
            print(scope);
            print(" => ");
            print(prefix);
            print(" => ");
            println(message);
          }

          hof(prefixed);
        }
        """.trimIndent(),
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

        use Std.IO;

        fun hof(f: *Char -> ()) -> () {
          f("String (hof)");
        }

        fun prefixed(prefix: *Char, message: *Char) -> () {
          print(prefix);
          print(" => ");
          println(message);
        }

        fun main(argc: Int32, argv: **Char) -> () {
          let f = prefixed("info");
          hof(f);
        }
        """.trimIndent(),
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test passing hof closure with currying by reference 2 times`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun hof(f: *Char -> ()) -> () {
          f("String (hof)");
        }

        fun prefixed(prefix: *Char, message: *Char) -> () {
          print(prefix);
          print(" => ");
          println(message);
        }

        fun main(argc: Int32, argv: **Char) -> () {
          let f = prefixed("info");
          hof(f);
          hof(f);
        }
        """.trimIndent(),
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test passing hof closure with currying by reference and exec the reference before hof`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun hof(f: *Char -> ()) -> () {
          f("String (hof)");
        }

        fun prefixed(prefix: *Char, message: *Char) -> () {
          print(prefix);
          print(" => ");
          println(message);
        }

        fun main(argc: Int32, argv: **Char) -> () {
          let f = prefixed("info");
          f("hello before");
          hof(f);
        }
        """.trimIndent(),
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test passing hof closure with currying by reference and exec the reference after hof`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun hof(f: *Char -> ()) -> () {
          f("String (hof)");
        }

        fun prefixed(prefix: *Char, message: *Char) -> () {
          print(prefix);
          print(" => ");
          println(message);
        }

        fun main(argc: Int32, argv: **Char) -> () {
          let f = prefixed("info");
          hof(f);
          f("hello after");
        }
        """.trimIndent(),
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
