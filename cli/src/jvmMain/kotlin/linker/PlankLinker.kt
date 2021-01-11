package com.lorenzoog.jplank.linker

import com.lorenzoog.jplank.message.MessageRenderer
import com.lorenzoog.jplank.utils.ProcessUtils
import pw.binom.io.file.File
import pw.binom.io.file.asJFile
import pw.binom.io.file.nameWithoutExtension
import pw.binom.io.file.parent

class PlankLinker(
  override val opts: LinkerOpts,
  private val renderer: MessageRenderer
) : Linker {
  private val runtime = Runtime.getRuntime()

  override fun generateStdlibObjects(): Boolean {
    runtime.exec(opts.buildCMakeCommand()).also {
      if (opts.debug) {
        ProcessUtils.printOutput(it)
      }

      if (it.waitFor() != 0) {
        return false
      }
    }

    runtime.exec(opts.makePath, arrayOf(), opts.cmakeBuildDir.asJFile).also {
      if (opts.debug) {
        ProcessUtils.printOutput(it)
      }

      if (it.waitFor() != 0) {
        return false
      }
    }

    renderer.info("Successfully generated stdlib objects")

    return true
  }

  override fun generateObject(file: File): Pair<Int, File> {
    val objFile = File(file.parent!!, "${file.nameWithoutExtension}.o")
    val exec = runtime.exec(opts.buildLLCCommand(file, objFile))
    val exitCode = exec.waitFor()
    val name = file.nameWithoutExtension

    if (opts.debug) {
      ProcessUtils.printOutput(exec)
    }

    if (exitCode != 0) {
      return exitCode to objFile
    }

    renderer.info("Generated $name.o file")

    return exitCode to objFile
  }

  override fun linkObjects(targets: List<File>, name: String): Int {
    val files = targets.map { it.path }
    val exec = runtime.exec(opts.buildLinkCommand(files, name))
    val exitCode = exec.waitFor()

    if (opts.debug) {
      ProcessUtils.printOutput(exec)
    }

    return exitCode
  }
}
