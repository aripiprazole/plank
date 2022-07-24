package org.plank.codegen.pkg

import java.io.File
import org.plank.analyzer.analyze
import org.plank.analyzer.resolver.FileScope
import org.plank.shared.depthFirstSearch
import org.plank.shared.nameWithoutExtension
import org.plank.syntax.debug.dumpTree
import org.plank.syntax.element.PlankFile
import org.plank.syntax.message.lineSeparator

fun Package.compileBinary(): File {
  verbose("Selected home: ${options.plankHome}")
  verbose("Current workdir: ${options.workingDir}")

  generateStdlibObjects()

  tree.dependencies
    .depthFirstSearch(main.module)
    .asSequence()
    .mapNotNull(tree::findModule)
    .map(org.plank.analyzer.resolver.Module::scope)
    .filterIsInstance<FileScope>()
    .map(FileScope::file)
    .map { generateIR(it) }
    .map { generateObject(it) }
    .toList()

  cmd(compileCommand(options.objects.listFiles().orEmpty().toList(), options.output.toString()))

  return options.output
}

private fun Package.generateObject(file: File): File {
  val obj = options.objects.child("${file.nameWithoutExtension}.o")

  cmd(linkCommand(file, obj))

  logger.verbose("Generated ${file.nameWithoutExtension}.o")

  return obj
}

private fun Package.generateIR(file: PlankFile): File {
  val target = options.ir.child("${file.realFile.nameWithoutExtension}.ll")

  if (options.debug.plainAstDebug) {
    logger.debug("Plain AST:")
    logger.debug(main.dumpTree())
    logger.debug()
  }

  compile(file, ::analyze, options.debug, tree, logger).toString().also { ir ->
    if (options.debug.llvmIrDebug) {
      logger.debug("Llvm IR:")
      logger.debug(ir)
      logger.debug()
    }
  }
    .also(target::writeText)

  return target
}

private fun Package.generateStdlibObjects() {
  options.runtime.listFiles()
    .orEmpty()
    .filter { it.extension == "cpp" }
    .forEach { file ->
      val target = options.objects.resolve("${file.nameWithoutExtension}.o")

      cmd(compileStdlibFile(file, target))
    }

  logger.verbose("Successfully generated stdlib objects")
}

fun Package.compileStdlibFile(file: File, target: File): Command {
  return Command.of(options.linker)
    .arg("-g")
    .arg("-O3")
    .arg("-c $file")
    .arg("-o $target")
}

fun Package.linkCommand(file: File, target: File): Command {
  return Command.of(options.linker)
    .arg("-c $file")
    .arg("-o $target")
}

fun Package.compileCommand(files: List<File>, name: String): Command {
  return Command.of(options.linker)
    .arg("-o $name")
    .arg("-v ${files.joinToString(" ")}")
}

private fun Package.cmd(command: Command) {
  val output = command.exec()

  if (options.debug.linkerVerbose) {
    output.split(lineSeparator).forEach {
      verbose(it)
    }
  }
}
