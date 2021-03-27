package com.lorenzoog.plank.cli.compiler

import com.lorenzoog.plank.analyzer.BindingContext
import com.lorenzoog.plank.analyzer.FileScope
import com.lorenzoog.plank.analyzer.Module
import com.lorenzoog.plank.cli.pkg.Package
import com.lorenzoog.plank.cli.utils.child
import com.lorenzoog.plank.cli.utils.children
import com.lorenzoog.plank.cli.utils.printOutput
import com.lorenzoog.plank.compiler.PlankLLVM
import com.lorenzoog.plank.grammar.element.PlankFile
import com.lorenzoog.plank.grammar.message.MessageRenderer
import com.lorenzoog.plank.shared.depthFirstSearch
import kotlin.io.path.ExperimentalPathApi
import pw.binom.io.file.File
import pw.binom.io.file.asJFile
import pw.binom.io.file.nameWithoutExtension
import pw.binom.io.file.write
import pw.binom.io.utf8Appendable

@ExperimentalPathApi
class PlankCompiler(
  private val pkg: Package,
  private val context: BindingContext,
  private val compiler: PlankLLVM,
  private val renderer: MessageRenderer
) {
  private val options = pkg.options

  fun compile() {
    generateStdlibObjects()

    pkg.tree.dependencies
      .depthFirstSearch(pkg.main.module)
      .asSequence()
      .mapNotNull(pkg.tree::findModule)
      .map(Module::scope)
      .filterIsInstance<FileScope>()
      .map(FileScope::file)
      .onEach(::validate)
      .map(::generateIR)
      .toList()

    generateObject(pkg.main.realFile)

    exec(compileCommand(options.objects.children.map(File::path), options.output.path))
  }

  private fun validate(file: PlankFile) {
    if (!file.isValid) {
      throw CompileError.SyntaxViolations(file.violations)
    }

    context.analyze(file)

    if (!context.isValid) {
      throw CompileError.BindingViolations(context.violations)
    }
  }

  private fun generateObject(file: File): File {
    val obj = options.objects.child("${file.nameWithoutExtension}.o")

    exec(linkCommand(file, obj))

    renderer.info("Generated ${file.nameWithoutExtension}.o")

    return obj
  }

  private fun generateIR(file: PlankFile): File {
    val target = options.ir.child("${file.realFile.nameWithoutExtension}.ll")

    compiler.initialize(file)
    compiler.compile(file)

    target.write()
      .utf8Appendable()
      .append(compiler.context.module.getAsString())

    if (compiler.context.errors.isNotEmpty()) {
      throw CompileError.IRViolations(compiler.module, compiler.context.errors)
    }

    return target
  }

  private fun generateStdlibObjects() {
    exec(compileStdlibCommand())

    ProcessBuilder(options.make)
      .directory(options.runtimeTarget.asJFile)

    renderer.info("Successfully generated stdlib objects")
  }

  private fun exec(command: String) {
    val process = Runtime.getRuntime().exec(command)
    if (options.debug) {
      process.printOutput()
    }

    val exitCode = process.waitFor()
    if (exitCode != 0) {
      throw CompileError.FailedCommand(command, exitCode)
    }
  }

  private fun compileStdlibCommand(): String {
    return listOf(
      options.cmake,
      "-S ${options.runtime.path}",
      "-B ${options.runtimeTarget.path}",
      "-DTARGET_OBJECTS_DIR=${options.objects.path}",
    ).joinToString(" ")
  }

  private fun linkCommand(file: File, target: File): String {
    return listOf(
      options.linker,
      "-c ${file.path}",
      "-o ${target.path}"
    ).joinToString(" ")
  }

  private fun compileCommand(files: List<String>, name: String): String {
    return listOf(
      options.linker,
      "-o $name",
      "-v ${files.joinToString(" ")}"
    ).joinToString(" ")
  }
}
