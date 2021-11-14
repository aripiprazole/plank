package com.gabrielleeg1.plank.cli.compiler

import com.gabrielleeg1.plank.analyzer.BindingContext
import com.gabrielleeg1.plank.analyzer.FileScope
import com.gabrielleeg1.plank.analyzer.Module
import com.gabrielleeg1.plank.cli.pkg.Package
import com.gabrielleeg1.plank.cli.utils.child
import com.gabrielleeg1.plank.cli.utils.children
import com.gabrielleeg1.plank.cli.utils.printOutput
import com.gabrielleeg1.plank.compiler.LlvmBackend
import com.gabrielleeg1.plank.compiler.instructions.CodegenError
import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.grammar.message.CompilerLogger
import com.gabrielleeg1.plank.shared.Left
import com.gabrielleeg1.plank.shared.depthFirstSearch
import kotlin.io.path.ExperimentalPathApi
import pw.binom.io.file.File
import pw.binom.io.file.append
import pw.binom.io.file.extension
import pw.binom.io.file.nameWithoutExtension

@ExperimentalPathApi
class PlankCompiler(
  private val pkg: Package,
  private val context: BindingContext,
  private val compiler: LlvmBackend,
  private val renderer: CompilerLogger
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
      .map(::generateObject)
      .toList()

    exec(compileCommand(options.objects.children.map(File::path), options.output.path))
  }

  private fun validate(file: PlankFile) {
    if (!file.isValid) {
      throw CompileError.SyntaxViolations(file.violations)
    }

    context.analyze(file)

    if (!context.isValid) {
      throw CompileError.BindingViolations(context.violations.toList())
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

    compiler.initialize(file, options.debug)

    val results = compiler.compile(file)
    val errors = results.filterIsInstance<Left<CodegenError>>().map { it.a }

    target.append(compiler.context.module.getAsString())

    if (errors.isNotEmpty()) {
      throw CompileError.IRViolations(compiler.module, errors)
    }

    return target
  }

  private fun generateStdlibObjects() {
    options.runtime.list()
      .filter { it.extension == "cpp" }
      .forEach { file ->
        val target = options.objects.child("${file.nameWithoutExtension}.o")

        exec(compileStdlibFile(file, target))
      }

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

  private fun compileStdlibFile(file: File, target: File): String {
    return listOf(
      options.linker,
      "-g",
      "-O3",
      "-c ${file.path}",
      "-o ${target.path}"
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
