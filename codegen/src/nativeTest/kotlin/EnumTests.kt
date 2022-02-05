package org.plank.codegen

import kotlin.test.Test

class EnumTests {
  @Test
  fun `test declaring linked list enum`() {
    TestCompilation
      .of(
        """
        module Main;

        type List =
          | Cons(*Char, List)
          | Nil;

        fun main(argc: Int32, argv: **Char): Void {
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test partial instantiate linked list enum`() {
    TestCompilation
      .of(
        """
        module Main;

        type List =
          | Cons(*Char, List)
          | Nil;

        fun main(argc: Int32, argv: **Char): Void {
          let cons = Cons("hello");
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test instantiate linked list enum`() {
    TestCompilation
      .of(
        """
        module Main;

        type List =
          | Cons(*Char, List)
          | Nil;

        fun main(argc: Int32, argv: **Char): Void {
          let cons = Cons("hello", Nil);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test basic pattern matching with linked list enum`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        type List =
          | Cons(*Char, List)
          | Nil;

        fun print_list(list: List) {
          match list {
            Cons(value, next) => println("cons"),
            Nil() => println("nil")
          };
        }

        fun main(argc: Int32, argv: **Char): Void {
          print_list(Cons("hello", Nil));
          print_list(Nil);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test returning pattern matching with linked list enum with unit type`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        type List =
          | Cons(*Char, List)
          | Nil;

        fun print_list(list: List): Void {
          return match list {
            Cons(value, next) => println("cons"),
            Nil() => println("nil")
          };
        }

        fun main(argc: Int32, argv: **Char): Void {
          print_list(Cons("hello", Nil));
          print_list(Nil);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test returning pattern matching with linked list enum`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        type List =
          | Cons(*Char, List)
          | Nil;

        fun show(list: List): *Char {
          return match list {
            Cons(value, next) => "cons",
            Nil() => "nil"
          };
        }

        fun main(argc: Int32, argv: **Char): Void {
          println(show(Cons("hello", Nil)));
          println(show(Nil));
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test basic pattern matching with linked list enum accessing its fields`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        type List =
          | Cons(*Char, List)
          | Nil;

        fun print_list(list: List) {
          match list {
            Cons(value, next) => println(value),
            Nil() => println("nil")
          };
        }

        fun main(argc: Int32, argv: **Char): Void {
          print_list(Cons("hello", Nil));
          print_list(Nil);
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }

  @Test
  fun `test returning value pattern matching with linked list enum`() {
    TestCompilation
      .of(
        """
        module Main;

        import Std.IO;

        type List =
          | Cons(*Char, List)
          | Nil;

        fun show(list: List): *Char {
          return match list {
            Cons(value, next) => value,
            Nil() => "nil"
          };
        }

        fun main(argc: Int32, argv: **Char): Void {
          println(show(Cons("hello", Nil)));
          println(show(Nil));
        }
        """.trimIndent()
      )
      .debugAll()
      .runTest {
        expectSuccess()
      }
  }
}
