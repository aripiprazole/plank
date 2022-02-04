package org.plank.codegen.pkg

import org.plank.codegen.DebugOptions
import org.plank.syntax.element.PlankFile
import org.plank.syntax.message.CompilerLogger
import org.plank.syntax.message.SimpleCompilerLogger
import pw.binom.io.file.File

data class CompileOptions(val plankHome: File) {
  var debug = DebugOptions()

  var emitIR = false
  var logger: CompilerLogger = SimpleCompilerLogger()

  var linker = locateBinary("clang++")
  var output = File("main")

  var workingDir: File = createTempDirectory("plank")

  val objects by lazy { workingDir.child("objects", recreate = true, dir = true) }
  val ir by lazy { workingDir.child("ir", recreate = true, dir = true) }

  /** TODO: use a package manager */
  val stdlib by lazy {
    plankHome.child("stdlib").list()
      .filter { it.path.endsWith(".plank") }
      .map { PlankFile.of(it) }
  }

  var runtime = plankHome.child("runtime")

  fun debug(block: DebugOptions.() -> Unit) {
    debug.block()
  }

  override fun hashCode(): Int = super.hashCode()

  override fun equals(other: Any?): Boolean = super.equals(other)

  override fun toString(): String = "CompileOptions"
}
