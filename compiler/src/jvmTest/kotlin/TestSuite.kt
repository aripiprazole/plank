package com.gabrielleeg1.plank.compiler

import org.junit.jupiter.api.Test

class TestSuite {
  @Test
  fun `test call`() {
    TestCompilation
      .of(
        """
        @external("PLANK_INTERNAL_println")
        fun println(message: *Char): Void

        fun main(argc: Int32, argv: **Char): Void {
          println("Hello, world!");
        }
        """.trimIndent()
      )
      .expectSuccess()
  }

  @Test
  fun `test structs`() {
    TestCompilation
      .of(
        """
        @external("PLANK_INTERNAL_println")
        fun println(message: *Char): Void

        type Person = {mutable name: *Char};

        fun main(argc: Int32, argv: **Char): Void {
          let person = Person{name: "Gabrielle"};
          println(person.name);
        }
        """.trimIndent()
      )
      .expectSuccess()
  }

  @Test
  fun `test struct set`() {
    TestCompilation
      .of(
        """
        @external("PLANK_INTERNAL_println")
        fun println(message: *Char): Void

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
      .expectSuccess()
  }
}
