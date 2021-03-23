package com.lorenzoog.jplank.compiler

import com.lorenzoog.jplank.analyzer.BindingContext
import com.lorenzoog.jplank.analyzer.render
import com.lorenzoog.jplank.element.PlankFile
import com.lorenzoog.jplank.grammar.render
import com.lorenzoog.jplank.linker.Linker
import com.lorenzoog.jplank.message.MessageRenderer
import pw.binom.io.file.File
import pw.binom.io.file.asBFile
import pw.binom.io.file.asJFile
import pw.binom.io.file.name
import pw.binom.io.file.nameWithoutExtension
import pw.binom.io.file.write

class PlankCompiler(
  private val linker: Linker,
  private val context: BindingContext,
  private val compiler: PlankLLVM,
  private val renderer: MessageRenderer
) {
  fun generateIR(files: List<File>): Boolean {
    files.map {
      val srcFile = File(it.path)
      val irFile = File(linker.opts.bytecodeDir, "${srcFile.nameWithoutExtension}.ll")

      if (!generateIR(srcFile, irFile)) {
        renderer.warning("Could not generate IR File for $it")
        return false
      }
    }

    return true
  }

  fun generateBinary(name: String): Boolean {
    if (!linker.generateStdlibObjects()) {
      renderer.warning("Failed to generate stdlib objects")
      return false
    }

    linker.opts.bytecodeDir.asJFile.listFiles().orEmpty()
      .map { it.asBFile }
      .forEach { file ->
        val (exitCode, generatedFile) = linker.generateObject(file)

        if (exitCode == 0) {
          File(linker.opts.objectsDir, generatedFile.name).also {
            generatedFile.renameTo(it)
          }
        } else {
          renderer.warning("Failed tom generate ${generatedFile.name} with exit code $exitCode")
          return false
        }
      }

    val objects = linker.opts.objectsDir.asJFile
      .listFiles()
      .orEmpty()
      .map { it.asBFile }

    val exitCode = linker.linkObjects(objects, name)
    if (exitCode != 0) {
      renderer.warning("Failed to link $name with exit code $exitCode")
      return false
    }

    return true
  }

  private fun generateIR(file: File, target: File): Boolean {
    val pkFile = PlankFile.of(file)
    if (!validate(pkFile)) {
      return false
    }

    compiler.initialize(pkFile)
    compiler.compile(pkFile)

    target.asJFile.writeText(compiler.context.module.getAsString())
    if (compiler.context.errors.isNotEmpty()) {
      compiler.context.errors.forEach { (element, message) ->
        renderer.severe(message, element?.location)
      }

      renderer.severe("Internal errors occurred")
      return false
    }

    renderer.info("Successfully compiled ${pkFile.module}")

    return true
  }

  private fun validate(plankFile: PlankFile): Boolean {
    if (!plankFile.isValid) {
      plankFile.violations.render(renderer)

      renderer.severe("Resolve the syntax issues above before compile")
      return false
    }

    context.analyze(plankFile)
    context.violations.render(renderer)

    if (!context.isValid) {
      renderer.severe("Resolve the binding issues above before compile")
      return false
    }

    return true
  }
}
