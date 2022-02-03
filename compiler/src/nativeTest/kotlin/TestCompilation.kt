@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package org.plank.compiler

import org.plank.analyzer.BindingViolation
import org.plank.compiler.pkg.AnalyzerError
import org.plank.compiler.pkg.Command
import org.plank.compiler.pkg.CommandFailedException
import org.plank.compiler.pkg.DebugOptions
import org.plank.compiler.pkg.Package
import org.plank.compiler.pkg.SyntaxError
import org.plank.compiler.pkg.child
import org.plank.compiler.pkg.compileBinary
import org.plank.compiler.pkg.createTempDirectory
import org.plank.compiler.pkg.exec
import org.plank.compiler.pkg.locateBinary
import org.plank.grammar.mapper.SyntaxViolation
import org.plank.grammar.message.SimpleCompilerLogger
import org.plank.grammar.message.lineSeparator
import org.plank.llvm4k.LLVMError
import pw.binom.io.file.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

class TestCompilation(
  private val pkg: Package,
  private val syntaxViolations: Set<SyntaxViolation>,
  private val bindingViolations: Set<BindingViolation>,
  private val exitCode: Int,
) {
  fun expectSyntaxViolation(message: String): TestCompilation = apply {
    assertNotNull(syntaxViolations.find { it.message == message })
  }

  fun expectBindingViolation(message: String): TestCompilation = apply {
    assertNotNull(bindingViolations.find { it.message == message })
  }

  fun expectExitCode(actual: Int): TestCompilation = apply {
    assertEquals(actual, exitCode)
  }

  fun expectSuccess(): TestCompilation = apply {
    if (syntaxViolations.isNotEmpty()) {
      pkg.logger.severe("Syntax violations:")
      syntaxViolations.forEach { it.render(pkg.logger) }
      pkg.logger.severe()
    }

    if (bindingViolations.isNotEmpty()) {
      pkg.logger.severe("Binding violations:")
      bindingViolations.forEach { it.render(pkg.logger) }
      pkg.logger.severe()
    }

    expectExitCode(0)

    if (bindingViolations.isNotEmpty() || syntaxViolations.isNotEmpty()) {
      fail("Compilation failed")
    }
  }

  class Builder(private val code: String) {
    private val options = DebugOptions()

    fun debugTree(): Builder = apply { options.treeDebug = true }
    fun debugPlainAst(): Builder = apply { options.plainAstDebug = true }
    fun debugResolvedAst(): Builder = apply { options.resolvedAstDebug = true }
    fun debugLlvmIR(): Builder = apply { options.llvmIrDebug = true }
    fun debugParser(): Builder = apply { options.parserDebug = true }
    fun debugCompilation(): Builder = apply { options.compilationDebug = true }
    fun linkerVerbose(): Builder = apply { options.linkerVerbose = true }

    fun debugAll(): Builder = apply {
//      debugTree()
//      debugPlainAst()
//      debugResolvedAst()
      debugLlvmIR()
//      debugParser()
      debugCompilation()
//      linkerVerbose()
    }

    @Suppress("PrintStackTrace", "TooGenericExceptionCaught")
    fun runTest(compilation: TestCompilation.() -> Unit = {}): TestCompilation {
      val pkg = Package(code, File("..")) {
        linker = locateBinary("clang++")
        workingDir = createTempDirectory("plank-test")
        output = workingDir.child("main")
        debug = options
        logger = SimpleCompilerLogger(debug = true, verbose = true)
      }

      var syntaxViolations: Set<SyntaxViolation> = emptySet()
      var bindingViolations: Set<BindingViolation> = emptySet()
      var exitCode = 0

      try {
        val binary = pkg.compileBinary()

        Command
          .of(binary).exec()
          .split(lineSeparator)
          .forEach {
            pkg.info(it)
          }
      } catch (error: CommandFailedException) {
        exitCode = error.exitCode
      } catch (error: AnalyzerError) {
        bindingViolations = error.violations
      } catch (error: SyntaxError) {
        syntaxViolations = error.violations
      } catch (error: CodegenError) {
        pkg.severe("Codegen Error: ${error.message}:")
        pkg.severe(error.context.currentModule.toString())
        (runCatching { error.context.currentModule.verify() }.exceptionOrNull() as? LLVMError)?.let { llvmError ->
          pkg.severe()
          pkg.severe("LLVM Error:")
          pkg.severe(llvmError.message)
        }
        fail(error.message)
      } catch (error: Throwable) {
        error.printStackTrace()
        throw error
      }

      return TestCompilation(pkg, syntaxViolations, bindingViolations, exitCode)
        .apply(compilation)
    }
  }

  companion object {
    fun of(code: String): Builder {
      return Builder(code)
    }
  }
}
