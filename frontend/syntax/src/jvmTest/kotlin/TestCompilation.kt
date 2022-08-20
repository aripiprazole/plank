@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package org.plank.syntax

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail
import org.plank.syntax.element.PlankFile
import org.plank.syntax.message.CompilerLogger

class TestCompilation(
  private val file: PlankFile,
  private val logger: CompilerLogger,
  private val syntaxViolations: Set<SyntaxViolation>,
) {
  fun expectSyntaxViolation(message: String): TestCompilation = apply {
    assertNotNull(syntaxViolations.find { it.message == message })
  }

  fun expectExitCode(actual: Int): TestCompilation = apply {
    assertEquals(actual, 0)
  }

  fun expectSuccess(): TestCompilation = apply {
    if (syntaxViolations.isNotEmpty()) {
      syntaxViolations.forEach { it.render(logger) }
    }

    expectExitCode(0)

    if (syntaxViolations.isNotEmpty()) {
      fail("Compilation failed")
    }
  }

  class Builder(private val code: String) {
    private var treeDebug = false
    private var plainAstDebug = false
    private var resolvedTreeDebug = false
    private var prettyDebug = false
    private var llvmIrDebug = false
    private var parserDebug = false
    private var compilationDebug = false
    private var linkerVerbose = false

    fun debugTree(): Builder = apply { treeDebug = true }
    fun debugPlainAst(): Builder = apply { plainAstDebug = true }
    fun debugResolvedTree(): Builder = apply { resolvedTreeDebug = true }
    fun debugPretty(): Builder = apply { prettyDebug = true }
    fun debugLlvmIR(): Builder = apply { llvmIrDebug = true }
    fun debugParser(): Builder = apply { parserDebug = true }
    fun debugCompilation(): Builder = apply { compilationDebug = true }
    fun linkerVerbose(): Builder = apply { linkerVerbose = true }

    fun debugAll(): Builder = apply {
      debugPretty()
      debugLlvmIR()
      debugCompilation()
    }

    @Suppress("PrintStackTrace", "TooGenericExceptionCaught")
    fun runTest(compilation: TestCompilation.() -> Unit = {}): TestCompilation {
      val logger = CompilerLogger()
      var syntaxViolations: Set<SyntaxViolation> = emptySet()
      val file: PlankFile

      try {
        file = PlankFile.of(code, "Anonymous.plank", logger = logger)

        syntaxViolations = file.violations.toSet()
      } catch (error: Throwable) {
        error.printStackTrace()
        throw error
      }

      return TestCompilation(file, logger, syntaxViolations).apply(compilation)
    }
  }

  companion object {
    fun of(code: String): Builder {
      return Builder(code)
    }
  }
}
