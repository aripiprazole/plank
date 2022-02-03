package org.plank.analyzer

import org.junit.jupiter.api.Test
import org.plank.syntax.element.PlankFile
import org.plank.syntax.message.SimpleCompilerLogger
import kotlin.test.assertEquals

class AnalyzerTest {
  @Test
  fun `test error message`() {
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

    val logger = SimpleCompilerLogger(debug = false, verbose = false)

    context.violations.forEach {
      it.render(logger)
    }

    assertEquals(2, context.violations.size)
  }
}
