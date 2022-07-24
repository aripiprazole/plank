package org.plank.codegen.pkg

import okio.Path
import okio.Path.Companion.toPath
import org.plank.codegen.DebugOptions
import org.plank.shared.list
import org.plank.syntax.element.PlankFile
import org.plank.syntax.message.CompilerLogger

data class CompileOptions(val plankHome: Path) {
  var debug = DebugOptions()

  var emitIR = false
  var logger: CompilerLogger = CompilerLogger()

  var linker = locateBinary("clang++")
  var output = "main".toPath()

  var workingDir: Path = createTempDirectory("plank")

  val objects by lazy { workingDir.child("objects", recreate = true, dir = true) }
  val ir by lazy { workingDir.child("ir", recreate = true, dir = true) }

  /** TODO: use a package manager */
  val stdlib by lazy {
    plankHome.child("stdlib").list()
      .filter { it.toString().endsWith(".plank") }
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
