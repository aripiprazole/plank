package com.gabrielleeg1.plank.cli

import com.gabrielleeg1.plank.cli.Target.Llvm
import com.gabrielleeg1.plank.compiler.compile.BindingError
import com.gabrielleeg1.plank.compiler.compile.CompileOptions
import com.gabrielleeg1.plank.compiler.compile.FailedCommand
import com.gabrielleeg1.plank.compiler.compile.IRDumpError
import com.gabrielleeg1.plank.compiler.compile.Package
import com.gabrielleeg1.plank.compiler.compile.SyntaxError
import com.gabrielleeg1.plank.compiler.compile.compileBinary
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.grammar.mapper.render
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import pw.binom.io.file.binom
import java.io.File
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createTempDirectory

@ExperimentalPathApi
fun main(args: Array<String>) {
  Plank().main(args)
}

enum class Target { Llvm }

@ExperimentalPathApi
class Plank : CliktCommand() {
  private val file by argument("file").help("The target file").file()

  private val target by option("--target")
    .convert { target ->
      when (target) {
        "llvm" -> Llvm
        else -> fail("Unrecognized target $target")
      }
    }
    .default(Llvm)

  private val pkgName by option("--pkg-name")
    .help("The package name")
    .default("Main")

  private val pkgKind by option("--pkg-kind")
    .help("The package kind")
    .convert { type ->
      when (type) {
        "lib" -> Package.Kind.Library
        "bin" -> Package.Kind.Binary
        else -> fail("Invalid package kind: $type")
      }
    }
    .default(Package.Kind.Binary)

  private val output by option("--output", "-O")
    .help("Output file")
    .file()
    .required()

  private val debug by option("--debug", "-D")
    .help("Sets the compiler on debug mode")
    .flag()

  private val verbose by option("--verbose", "-V")
    .help("Sets the compiler on verbose mode")
    .flag()

  private val emitIR by option("--emit-ir")
    .help("Emits the ir code when compiling")
    .flag()

  private val include by option("--include", "-I")
    .help("Include files")
    .convert { path -> PlankFile.of(path) }
    .multiple()

  override fun run() {
    val logger = ColoredLogger(verbose, debug, flush = true)

    val plankHome = System.getenv("PLANK_HOME")
      ?.let { File(it) }
      ?: return logger.severe("Define the PLANK_HOME before compile")

    val options = CompileOptions(plankHome).apply {
      debug = this@Plank.debug
      emitIR = this@Plank.emitIR
      dist = createTempDirectory("build_${pkgName}_${System.currentTimeMillis()}").toFile()

      output = this@Plank.output
    }

    val pkg = Package(
      name = pkgName,
      options = options,
      kind = when (pkgKind) {
        Package.Kind.Binary -> pkgKind
        Package.Kind.Library -> TODO("unsupported library kind yet")
      },
      main = PlankFile.of(file.binom),
      include = include + options.stdlib,
      logger = logger,
    )

    logger.verbose("Current workdir: ${options.dist}")

    try {
      pkg.compileBinary()
      logger.info("Successfully compiled $output")
    } catch (error: BindingError) {
      logger.severe("Please resolve the following issues before compile:")
      error.violations.forEach { it.render(logger) }
    } catch (error: IRDumpError) {
      logger.severe("Internal compiler error, please open an issue.")

      error.violations.map(CodegenViolation::render).forEach(logger::severe)

      logger.verbose("LLVM Module:")
      if (verbose) {
        println(error.module.getAsString())
      }
    } catch (error: SyntaxError) {
      logger.severe("Please resolve the following issues before compile:")
      error.violations.render(logger)
    } catch (error: FailedCommand) {
      logger.severe("Could not execute '${error.command}'. Failed with exit code: ${error.exitCode}") // ktlint-disable max-line-length
    } catch (error: Throwable) {
      logger.severe("${error::class.simpleName}: ${error.message}")
      if (verbose) {
        error.printStackTrace()
      }
    }
  }
}
