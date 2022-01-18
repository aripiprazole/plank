package com.gabrielleeg1.plank.compiler

import org.junit.jupiter.api.Test

class TestSuite {
  @Test
  fun `test currying`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun print_full_name(name: *Char, surname: *Char): Void {
          print(name);
          print(" ");
          println(surname);
        }

        fun main(argc: Int32, argv: **Char): Void {
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
  fun `test hof`() {
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
  fun `test nested access`() {
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

  @Test
  fun `test function ref`() {
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
  fun `test nesting functions`() {
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
  fun `test call`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        fun main(argc: Int32, argv: **Char): Void {
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
  fun `test structs`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        type Person = {mutable name: *Char};

        fun main(argc: Int32, argv: **Char): Void {
          let person = Person{name: "Gabrielle"};
          println(person.name);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test struct set`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        type Person = {mutable name: *Char};

        fun create_gabrielle(): *Person {
          return &Person{name: "Gabrielle"};
        }

        fun create_alfredo(): *Person {
          return &Person{name: "Alfredo"};
        }

        fun create_gerson(): Person {
          return Person{name: "Gerson"};
        }

        fun main(argc: Int32, argv: **Char): Void {
          println(create_gerson().name);
          let mutable person = *create_gabrielle();
          println(person.name);
          person := *create_alfredo();
          println(person.name);
          person.name := "Alberto";
          println(person.name);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test nesting hof access`() {
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
}
