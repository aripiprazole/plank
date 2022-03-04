package org.plank.codegen

import kotlin.test.Test

class GenericTests {
  @Test
  fun `test creating simple generic functions`() {
    TestCompilation
      .of(
        """
        module Main;

        fun generic_fn(value: a) {
        }

        fun main(argc: Int32, argv: **Char) {
          generic_fn("");
          generic_fn(10);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
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
  fun `test creating enum with generics and applying a constant to call and pattern match`() {
    TestCompilation
      .of(
        """
        module Main;

        use Std.IO;

        enum Maybe[a] {
          Just(a),
          Nothing
        }

        fun show(maybe: Maybe[Int32]) {
          match maybe {
            Just(value) => print_int(value),
            Nothing => println("Nothing")
          }
        }

        fun main(argc: Int32, argv: **Char) {
          show(Just(10));
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

        enum Maybe[a] {
          Just(a),
          Nothing
        }

        fun main(argc: Int32, argv: **Char) {
          Just("yes");
          Just(123);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test creating recursive enum with generics and applying a constant to call`() {
    TestCompilation
      .of(
        """
        module Main;

        enum List[a] {
          Cons(a, List[a]),
          Nil
        }

        fun main(argc: Int32, argv: **Char) {
          Cons(10, Nil);
        }
        """.trimIndent()
      )
      .debugAll()
      .debugResolvedTree()
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
