package org.plank.codegen

import kotlin.test.Test

class CurryingTests {
  @Test
  fun `test basic currying`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun main(argc: Int32, argv: **Char) -> () {
          println("Hello, world");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test call currying without call chain`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun print_full_name(name: *Char, surname: *Char) {
          print(name);
          print(" ");
          println(surname);
        }

        fun main(argc: Int32, argv: **Char) -> () {
          print_full_name("Isabela", "Freitas");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test call currying`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun print_full_name(name: *Char, surname: *Char) {
          print(name);
          print(" ");
          println(surname);
        }

        fun main(argc: Int32, argv: **Char) -> () {
          print_full_name("Isabela")("Freitas");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test call currying 2 times`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun print_full_name(name: *Char, surname: *Char) {
          print(name);
          print(" ");
          println(surname);
        }

        fun main(argc: Int32, argv: **Char) -> () {
          print_full_name("Isabela")("Freitas");
          print_full_name("Isabela")("Freitas");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test partial apply curried function`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun print_full_name(name: *Char, surname: *Char) {
          print(name);
          print(" ");
          println(surname);
        }

        fun main(argc: Int32, argv: **Char) -> () {
          let print_surname = print_full_name("Isabela");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test call partial applied curried function`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun print_full_name(name: *Char, surname: *Char) {
          print(name);
          print(" ");
          println(surname);
        }

        fun main(argc: Int32, argv: **Char) -> () {
          let print_surname = print_full_name("Isabela");
          print_surname("Freitas");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test call partial applied curried function 2 times`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun print_full_name(name: *Char, surname: *Char) {
          print(name);
          print(" ");
          println(surname);
        }

        fun main(argc: Int32, argv: **Char) -> () {
          let print_surname = print_full_name("Isabela");
          print_surname("Freitas");
          print_surname("Freitas");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test basic currying with empty parameters function`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        fun empty() -> () {
          println("Hello, world!");
        }

        fun main(argc: Int32, argv: **Char) -> () {
          empty();
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
