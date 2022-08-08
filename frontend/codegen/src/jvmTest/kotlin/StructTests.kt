package org.plank.codegen

import kotlin.test.Test

class StructTests {
  @Test
  fun `test instantiating struct`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        type Person = {mutable name: *Char};

        fun main(argc: Int32, argv: **Char) -> () {
          let person = Person{name: "Gabrielle"};
        }
        """.trimIndent(),
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test get expression with struct`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        type Person = {mutable name: *Char};

        fun main(argc: Int32, argv: **Char) -> () {
          let person = Person{name: "Gabrielle"};
          println(person.name);
        }
        """.trimIndent(),
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test get and expressions with struct`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        type Person = {mutable name: *Char};

        fun create_gabrielle() -> *Person {
          &Person{name: "Gabrielle"}
        }

        fun create_alfredo() -> *Person {
          &Person{name: "Alfredo"}
        }

        fun create_gerson() -> Person {
          Person{name: "Gerson"}
        }

        fun main(argc: Int32, argv: **Char) -> () {
          println(create_gerson().name);
          let mutable person = *create_gabrielle();
          println(person.name);
          person := *create_alfredo();
          println(person.name);
          person.name := "Alberto";
          println(person.name);
        }
        """.trimIndent(),
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
