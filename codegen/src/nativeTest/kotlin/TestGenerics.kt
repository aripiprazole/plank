package org.plank.codegen

import kotlin.test.Test

class TestGenerics {
  @Test
  fun `test creating enum with generics`() {
    TestCompilation
      .of(
        """
        module Main;

        enum List[a] {
          Cons(a, List[a]),
          Nil
        }

        fun show_list(list: List[a]) {
        }

        fun main(argc: Int32, argv: **Char) {
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test creating enum with generics and applying a constant to call`() {
    TestCompilation
      .of(
        """
        module Main;

        enum List[a] {
          Cons(a, List[a]),
          Nil
        }

        fun show_list(list: List[a]) {}

        fun main(argc: Int32, argv: **Char) {
          show_list(Nil);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test creating enum with generics and applying a constant to call with arguments`() {
    TestCompilation
      .of(
        """
        module Main;

        enum List[a] {
          Cons(a, List[a]),
          Nil
        }

        fun show_list(list: List[a]) {}

        fun main(argc: Int32, argv: **Char) {
          show_list(Cons(1, Nil));
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test creating struct with generics and applying call with arguments`() {
    TestCompilation
      .of(
        """
        module Main;

        type Person[a] = {
          name: a,
          age: Int32
        }

        fun show_person(person: Person[a]) {}

        fun main(argc: Int32, argv: **Char) {
          let person = Person{name: "John", age: 42};
          show_person(person);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
