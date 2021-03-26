package com.lorenzoog.jplank.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import com.lorenzoog.jplank.analyzer.DefaultBindingContext
import com.lorenzoog.jplank.analyzer.render
import com.lorenzoog.jplank.compiler.CompileError
import com.lorenzoog.jplank.compiler.CompilerOptions
import com.lorenzoog.jplank.compiler.PlankCompiler
import com.lorenzoog.jplank.compiler.PlankLLVM
import com.lorenzoog.jplank.compiler.Target
import com.lorenzoog.jplank.element.PlankFile
import com.lorenzoog.jplank.grammar.render
import com.lorenzoog.jplank.message.ColoredMessageRenderer
import com.lorenzoog.jplank.pkg.Package
import com.lorenzoog.jplank.utils.asFile
import com.lorenzoog.jplank.utils.currentFile
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createTempDirectory
import pw.binom.io.file.File
import pw.binom.io.file.asBFile

class Plank : CliktCommand() {
  private val file by argument("file")
    .help("The target file")
    .convert { File(it) }

  private val target by option("--target")
    .convert { target ->
      when (target) {
        "llvm" -> Target.Llvm
        else -> fail("Unreconized target $target")
      }
    }
    .default(Target.Llvm)

  private val pkgName by option("--pkg-name")
    .help("The package name")
    .default("Main")

  private val pkgRoot by option("--pkg-root")
    .help("The package source root")
    .convert { path -> File(path) }
    .default(currentFile)

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

  private val pkgPrefix by option("--pkg-prefix").help("The package prefix")

  private val output by option("--output", "-O")
    .help("Output file")
    .file()
    .convert { it.asBFile }
    .required()

  private val debug by option("--debug", "-D")
    .help("Sets the compiler on debug mode")
    .flag()

  private val emitIR by option("--emit-ir")
    .help("Emits the ir code when compiling")
    .flag()

  private val include by option("--include", "-I")
    .help("Include files")
    .convert { path -> PlankFile.of(File(path)) }
    .multiple()

  @ExperimentalPathApi
  override fun run() {
    val renderer = ColoredMessageRenderer(flush = true)

    val plankHome = System.getenv("PLANK_HOME")
      ?.let { File(it) }
      ?: return renderer.severe("Define the PLANK_HOME before compile")

    val pkg = Package(
      name = pkgName,
      prefix = pkgPrefix,
      root = pkgRoot,
      options = CompilerOptions(pkgRoot, plankHome).apply {
        debug = this@Plank.debug
        emitIR = this@Plank.emitIR
        dist = "build_${pkgName}_${System.currentTimeMillis()}"
          .let(::createTempDirectory)
          .asFile()

        output = this@Plank.output
      },
      kind = when (pkgKind) {
        Package.Kind.Binary -> pkgKind
        Package.Kind.Library -> TODO("unsupported library kind yet")
      },
      main = PlankFile.of(file),
      include = include
    )

    val context = DefaultBindingContext(pkg.tree)
    val llvm = PlankLLVM(pkg.tree, context)

    val compiler = PlankCompiler(pkg, context, llvm, renderer)
    try {
      compiler.compile()
      renderer.info("Successfully compiled $output")
    } catch (error: Throwable) {
      when (error) {
        is CompileError.BindingViolations -> {
          renderer.severe("Please resolve the following issues before compile:")
          error.violations.render(renderer)
        }

        is CompileError.IRViolations -> {
          renderer.severe("Internal compiler error, please open an issue.")
          error.violations.forEach { (element, message) ->
            renderer.severe(message, element?.location)
          }
          if (debug) {
            renderer.info("LLVM Module:")
            println(error.module.getAsString())
          }
        }

        is CompileError.SyntaxViolations -> {
          renderer.severe("Please resolve the following issues before compile:")
          error.violations.render(renderer)
        }

        is CompileError.FailedCommand -> {
          renderer.severe("Could not execute '${error.command}'. Failed with exit code: ${error.exitCode}") // ktlint-disable max-line-length
        }
        else -> {
          renderer.severe("${error::class.simpleName}: ${error.message}")
        }
      }
    }
  }
}
