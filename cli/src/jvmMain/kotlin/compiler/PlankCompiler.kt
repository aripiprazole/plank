package com.lorenzoog.jplank.compiler

import com.lorenzoog.jplank.analyzer.BindingContext
import com.lorenzoog.jplank.analyzer.FileScope
import com.lorenzoog.jplank.analyzer.Module
import com.lorenzoog.jplank.analyzer.depthFirstSearch
import com.lorenzoog.jplank.analyzer.render
import com.lorenzoog.jplank.element.PlankFile
import com.lorenzoog.jplank.message.MessageRenderer
import com.lorenzoog.jplank.pkg.Package
import com.lorenzoog.jplank.utils.child
import com.lorenzoog.jplank.utils.children
import com.lorenzoog.jplank.utils.printOutput
import pw.binom.io.file.File
import pw.binom.io.file.asJFile
import pw.binom.io.file.name
import pw.binom.io.file.nameWithoutExtension
import pw.binom.io.file.write
import pw.binom.io.utf8Appendable

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
      .depthFirstSearch(pkg.main)
      .asSequence()
      .mapNotNull(pkg.tree::findModule)
      .map(Module::scope)
      .filterIsInstance<FileScope>()
      .map(FileScope::file)
      .onEach(this::validate)
      .onEach(this::generateIR)
      .onEach(this::generateObject)
      .toList()

    compileCommand(options.objects.children.map(File::path), options.output.path)
      .exec()
  }

  private fun validate(file: PlankFile) {
    if (!file.isValid) {
      throw CompileError.SyntaxViolations(file.violations)
    }

    context.analyze(file)
    context.violations.render(renderer)

    if (!context.isValid) {
      throw CompileError.BindingViolations(context.violations)
    }
  }

  private fun generateObject(file: PlankFile) {
    val realFile = file.realFile
    val obj = realFile.child("${realFile.nameWithoutExtension}.o")

    linkCommand(realFile, obj).exec()

    renderer.info("Generated ${realFile.nameWithoutExtension}.o")
  }

  private fun generateIR(file: PlankFile) {
    val target = options.ir.child("${file.realFile.nameWithoutExtension}.ll")

    compiler.initialize(file)
    compiler.compile(file)

    target.write()
      .utf8Appendable()
      .append(compiler.context.module.getAsString())

    if (compiler.context.errors.isNotEmpty()) {
      throw CompileError.IRViolations(compiler.context.errors)
    }
  }

  private fun generateStdlibObjects() {
    compileStdlibCommand().exec()

    ProcessBuilder(options.make)
      .directory(options.stdlibTarget.asJFile)
      .exec()

    renderer.info("Successfully generated stdlib objects")
  }

  private fun ProcessBuilder.exec() {
    val process = start()
    if (options.debug) {
      process.printOutput()
    }

    val exitCode = process.waitFor()
    if (exitCode != 0) {
      throw CompileError.FailedCommand(command().joinToString(" "), exitCode)
    }
  }

  private fun compileStdlibCommand(): ProcessBuilder {
    return ProcessBuilder(options.cmake).command(
      "-S ${options.stdlib.path}",
      "-B ${options.stdlibTarget.path}",
      "-DTARGET_OBJECTS_DIR=${options.objects.path}",
      "-DCMAKE_BUILDTYPE=${if (options.debug) "Debug" else "Release"}"
    )
  }

  private fun linkCommand(file: File, target: File): ProcessBuilder {
    return ProcessBuilder(options.linker).command(
      "-c ${file.path}",
      "-o ${target.path}"
    )
  }

  private fun compileCommand(files: List<String>, name: String): ProcessBuilder {
    return ProcessBuilder(options.linker).command(
      "-o $name",
      "-v ${files.joinToString(" ")}"
    )
  }
}
