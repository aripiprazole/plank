package com.gabrielleeg1.plank.compiler

import org.junit.jupiter.api.Test

class CurryingTests {
  @Test
  fun `test basic currying`() {
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
}
