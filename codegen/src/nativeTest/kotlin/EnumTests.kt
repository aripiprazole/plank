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
}
