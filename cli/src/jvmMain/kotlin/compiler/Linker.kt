package com.lorenzoog.jplank.compiler

import com.lorenzoog.jplank.message.MessageRenderer
import com.lorenzoog.jplank.utils.printOutput
import pw.binom.io.file.File
import pw.binom.io.file.asJFile
import pw.binom.io.file.nameWithoutExtension
import pw.binom.io.file.parent

class Linker(val opts: LinkerOpts, private val renderer: MessageRenderer) {
  private val runtime = Runtime.getRuntime()

  fun generateStdlibObjects(): Boolean {
    runtime.exec(opts.buildCMakeCommand()).also { proc ->
      if (opts.debug) {
        proc.printOutput()
      }

      if (proc.waitFor() != 0) {
        return false
      }
    }

    val proc = runtime.exec(opts.makePath, arrayOf(), opts.cmakeBuildDir.asJFile)

    if (opts.debug) {
      proc.printOutput()
    }

    if (proc.waitFor() != 0) {
      return false
    }

    renderer.info("Successfully generated stdlib objects")

    return true
  }

  fun generateObject(file: File): Pair<Int, File> {
    val objFile = File(file.parent!!, "${file.nameWithoutExtension}.o")
    val proc = runtime.exec(opts.buildLLCCommand(file, objFile))

    val exitCode = proc.waitFor()
    val name = file.nameWithoutExtension

    if (opts.debug) {
      proc.printOutput()
    }

    if (exitCode != 0) {
      return exitCode to objFile
    }

    renderer.info("Generated $name.o file")

    return exitCode to objFile
  }

  fun linkObjects(targets: List<File>, name: String): Int {
    val files = targets.map { it.path }
    val proc = runtime.exec(opts.buildLinkCommand(files, name))
    val exitCode = proc.waitFor()

    if (opts.debug) {
      proc.printOutput()
    }

    return exitCode
  }
}
