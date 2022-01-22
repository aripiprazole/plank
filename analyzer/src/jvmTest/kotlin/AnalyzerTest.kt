package com.gabrielleeg1.plank.analyzer

import com.gabrielleeg1.plank.grammar.element.PlankFile
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AnalyzerTest {
  @Test
  fun `test working`() {
    val context = BindingContext(ModuleTree())

    context.analyze(
      PlankFile.of(
        """
        fun main(argc: Int32, argv: **Char) {
          println("Hello, world");
        }
        """.trimIndent()
      )
    )

    assertEquals(2, context.violations.size)
  }
}
