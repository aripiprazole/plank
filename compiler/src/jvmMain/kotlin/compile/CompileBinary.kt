package com.gabrielleeg1.plank.compiler.compile

import arrow.core.identity
import com.gabrielleeg1.plank.analyzer.FileScope
import com.gabrielleeg1.plank.analyzer.Module
import com.gabrielleeg1.plank.analyzer.analyze
import com.gabrielleeg1.plank.compiler.compile
import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.shared.depthFirstSearch
import pw.binom.io.file.nameWithoutExtension
import java.io.File

fun Package.compileBinary(): File {
  generateStdlibObjects()

  tree.dependencies
    .depthFirstSearch(main.module)
    .asSequence()
    .mapNotNull(tree::findModule)
    .map(Module::scope)
    .filterIsInstance<FileScope>()
    .map(FileScope::file) // TODO VALIDATE
    .map { generateIR(it) }
    .map { generateObject(it) }
    .toList()

  cmd(compileCommand(options.linker, options.objects.children.map { it.path }, options.output.path))

  return options.output
}

private fun Package.generateObject(file: File): File {
  val obj = options.objects.child("${file.nameWithoutExtension}.o")

  cmd(linkCommand(options.linker, file, obj))

  logger.info("Generated ${file.nameWithoutExtension}.o")

  return obj
}

private fun Package.generateIR(file: PlankFile): File {
  val target = options.ir.child("${file.realFile.nameWithoutExtension}.ll")

  target.writeText(
    compile(file, ::analyze)
      .fold(
        ifRight = ::identity,
        ifLeft = { (module, violations) -> throw IRDumpError(module, violations) },
      )
      .getAsString(),
  )

  return target
}

private fun Package.generateStdlibObjects() {
  options.runtime.children
    .filter { it.extension == "cpp" }
    .forEach { file ->
      val target = options.objects.child("${file.nameWithoutExtension}.o")

      cmd(compileStdlibFile(options.linker, file, target))
    }

  logger.info("Successfully generated stdlib objects")
}

private fun Package.cmd(command: String) {
  val process = Runtime.getRuntime().exec(command)
  if (options.debug) {
    process.printOutput()
  }

  val exitCode = process.waitFor()
  if (exitCode != 0) {
    throw FailedCommand(command, exitCode)
  }
}
