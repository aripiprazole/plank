package org.plank.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.check
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kotlin.system.exitProcess
import org.plank.analyzer.analyze
import org.plank.codegen.pkg.AnalyzerError
import org.plank.codegen.pkg.Package
import org.plank.codegen.pkg.SyntaxError
import org.plank.codegen.pkg.compile
import org.plank.llvm4k.OptimizationLevel
import org.plank.syntax.message.CompilerLogger
import pw.binom.io.file.File
import pw.binom.io.file.isExist

/**
 * TODO: add support for multiple files
 */
class PlankJIT : CliktCommand(
  name = "jit",
  printHelpOnEmptyArgs = true,
  treatUnknownOptionsAsArgs = true,
) {
  private val file: File by argument("file", "File to run in JIT mode")
    .convert { File(it) }
    .check(lazyMessage = { "The specified file: $it does not exist" }) { it.isExist }

  private val workingDir: File by option(help = "Working directory")
    .convert { File(it) }
    .default(File("."))
    .check(lazyMessage = { "The specified file: $it does not exist" }) { it.isExist }

  private val printLlvmModule: Boolean by option(help = "Print the llvm module").flag()

  private val optimizationLevel: OptimizationLevel by option("-O", help = "Optimization level")
    .convert { OptimizationLevel.values()[it.toInt()] }
    .default(OptimizationLevel.Aggressive)

  private val verbose: Boolean by option(help = "Enable compiler verbose mode").flag()

  private val debug: Boolean by option(help = "Enable compiler debug mode").flag()

  private val arguments by argument(help = "Program arguments").multiple()

  override fun run() {
    val pkg = Package(file, workingDir, false) {
      logger = CompilerLogger(
        debug = this@PlankJIT.debug || printLlvmModule,
        verbose = this@PlankJIT.verbose,
      )
    }

    runCatching {
      val module = compile(pkg.main, ::analyze, pkg.options.debug, Builtins.tree, pkg.logger)
      val main = module.getFunction("main") ?: pkg.crash("Unable to find entrypoint function")

      if (printLlvmModule) {
        pkg.debug(module.toString())
      }

      module
        .createJITExecutionEngine(optimizationLevel)
        .runFunctionAsMain(main, arguments.toTypedArray())
        .also(::exitProcess)
    }.onFailure { error ->
      when (error) {
        is AnalyzerError -> error.violations.forEach { it.render(pkg) }
        is SyntaxError -> error.violations.forEach { it.render(pkg) }
        else -> pkg.crash(error.message ?: error::class.simpleName ?: "Unknown error")
      }

      throw ProgramResult(1)
    }
  }
}
