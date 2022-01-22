@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.BindingViolation
import com.gabrielleeg1.plank.compiler.compile.BindingError
import com.gabrielleeg1.plank.compiler.compile.DebugOptions
import com.gabrielleeg1.plank.compiler.compile.IRDumpError
import com.gabrielleeg1.plank.compiler.compile.Package
import com.gabrielleeg1.plank.compiler.compile.SyntaxError
import com.gabrielleeg1.plank.compiler.compile.compileBinary
import com.gabrielleeg1.plank.compiler.compile.printOutput
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
import com.gabrielleeg1.plank.grammar.mapper.SyntaxViolation
import com.gabrielleeg1.plank.grammar.message.SimpleCompilerLogger
import java.nio.file.Paths
import kotlin.io.path.createTempDirectory
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

class TestCompilation(
  private val pkg: Package,
  private val syntaxViolations: List<SyntaxViolation>,
  private val bindingViolations: List<BindingViolation>,
  private val codegenViolations: List<CodegenViolation>,
  private val exitCode: Int,
) {
  fun expectSyntaxViolation(message: String): TestCompilation = apply {
    assertNotNull(syntaxViolations.find { it.message == message })
  }

  fun expectBindingViolation(message: String): TestCompilation = apply {
    assertNotNull(bindingViolations.find { it.message == message })
  }

  fun expectCodegenViolation(message: String): TestCompilation = apply {
//    assertNotNull(codegenViolations.find { it.message == message })
    TODO()
  }

  fun expectExitCode(actual: Int): TestCompilation = apply {
    assertEquals(actual, exitCode)
  }

  fun expectSuccess(): TestCompilation = apply {
    if (codegenViolations.isNotEmpty()) {
      pkg.logger.severe("Codegen violations:")
      codegenViolations.forEach { it.render(pkg.logger) }
      pkg.logger.severe()
    }

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

    if (bindingViolations.isNotEmpty() || codegenViolations.isNotEmpty() || syntaxViolations.isNotEmpty()) {
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
      debugTree()
      debugPlainAst()
      debugResolvedAst()
      debugLlvmIR()
      debugParser()
      debugCompilation()
      linkerVerbose()
    }

    fun runTest(compilation: TestCompilation.() -> Unit = {}): TestCompilation {
      val pkg = Package(code, Paths.get("..").toAbsolutePath().toFile()) {
        linker =
          "/home/gabi/Programs/swift-5.3.1-RELEASE-ubuntu20.04/usr/bin/clang++" // todo change linker
        workingDir = createTempDirectory("plank-test").toFile()
        output = workingDir.resolve("main")
        debug = options
        logger = SimpleCompilerLogger(debug = true, verbose = true)
      }

      var syntaxViolations: List<SyntaxViolation> = emptyList()
      var bindingViolations: List<BindingViolation> = emptyList()
      var codegenViolations: List<CodegenViolation> = emptyList()
      var exitCode = -1

      try {
        val binary = pkg.compileBinary()

        exitCode = java.lang.Runtime.getRuntime()
          .exec(binary.absolutePath)
          .printOutput(pkg.logger)
          .waitFor()
      } catch (error: BindingError) {
        bindingViolations = error.violations
      } catch (error: SyntaxError) {
        syntaxViolations = error.violations
      } catch (error: IRDumpError) {
        codegenViolations = error.violations
      }

      return TestCompilation(pkg, syntaxViolations, bindingViolations, codegenViolations, exitCode)
        .apply(compilation)
    }
  }

  companion object {
    fun of(code: String): Builder {
      return Builder(code)
    }
  }
}
