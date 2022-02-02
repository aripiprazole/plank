package com.gabrielleeg1.plank.compiler.pkg

import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.grammar.message.CompilerLogger
import com.gabrielleeg1.plank.grammar.message.SimpleCompilerLogger
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
  val stdlib = plankHome.child("stdlib").list()
    .filter { it.path.endsWith(".plank") }
    .map { PlankFile.of(it) }

  /** TODO: remove ffi from stdlib */
  var runtime = plankHome.child("runtime")

  fun debug(block: DebugOptions.() -> Unit) {
    debug.block()
  }

  override fun hashCode(): Int = super.hashCode()

  override fun equals(other: Any?): Boolean = super.equals(other)

  override fun toString(): String = "CompileOptions"
}
