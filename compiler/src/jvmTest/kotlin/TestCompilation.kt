@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.BindingViolation
import com.gabrielleeg1.plank.compiler.compile.BindingError
import com.gabrielleeg1.plank.compiler.compile.IRDumpError
import com.gabrielleeg1.plank.compiler.compile.Package
import com.gabrielleeg1.plank.compiler.compile.SyntaxError
import com.gabrielleeg1.plank.compiler.compile.compileBinary
import com.gabrielleeg1.plank.compiler.compile.printOutput
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
import com.gabrielleeg1.plank.grammar.mapper.SyntaxViolation
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
      codegenViolations.forEach { pkg.logger.severe(it.render()) }
      pkg.logger.severe("")
    }

    if (syntaxViolations.isNotEmpty()) {
      pkg.logger.severe("Syntax violations:")
      syntaxViolations.forEach { it.render(pkg.logger) }
      pkg.logger.severe("")
    }

    if (bindingViolations.isNotEmpty()) {
      pkg.logger.severe("Binding violations:")
      bindingViolations.forEach { it.render(pkg.logger) }
      pkg.logger.severe("")
    }

    expectExitCode(0)

    if (bindingViolations.isNotEmpty() || codegenViolations.isNotEmpty() || syntaxViolations.isNotEmpty()) {
      fail("Compilation failed")
    }
  }

  companion object {
    fun of(code: String): TestCompilation {
      val pkg = Package(code, Paths.get("..").toAbsolutePath().toFile()) {
        linker =
          "/home/gabi/Programs/swift-5.3.1-RELEASE-ubuntu20.04/usr/bin/clang++" // todo change linker
        dist = createTempDirectory("plank-test").toFile()
        output = dist.resolve("main")
        debug = true
        logger = ColoredLogger(errWriter = System.out)
      }

      var syntaxViolations: List<SyntaxViolation> = emptyList()
      var bindingViolations: List<BindingViolation> = emptyList()
      var codegenViolations: List<CodegenViolation> = emptyList()
      var exitCode = -1

      try {
        val binary = pkg.compileBinary()

        exitCode = Runtime.getRuntime().exec(binary.absolutePath).printOutput(pkg.logger).waitFor()
      } catch (error: BindingError) {
        bindingViolations = error.violations
      } catch (error: SyntaxError) {
        syntaxViolations = error.violations
      } catch (error: IRDumpError) {
        codegenViolations = error.violations
      }

      return TestCompilation(pkg, syntaxViolations, bindingViolations, codegenViolations, exitCode)
    }
  }
}
