package com.gabrielleeg1.plank.compiler.compile

import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.grammar.message.CompilerLogger
import com.gabrielleeg1.plank.grammar.message.SimpleCompilerLogger
import pw.binom.io.file.binom
import kotlin.io.path.createTempDirectory
import java.io.File

data class CompileOptions(val plankHome: File) {
  var debug = DebugOptions()

  var emitIR = false
  var logger: CompilerLogger = SimpleCompilerLogger()

  var linker = "clang++"
  var output = File("main")

  var dist: File = createTempDirectory().toFile()

  val objects by lazy { dist.child("objects", recreate = true, dir = true) }
  val ir by lazy { dist.child("ir", recreate = true, dir = true) }

  /** TODO: use a package manager */
  val stdlib = plankHome.child("stdlib").children
    .filter { it.path.endsWith(".plank") }
    .map { PlankFile.of(it.binom) }

  /** TODO: remove ffi from stdlib */
  var runtime = plankHome.child("runtime")

  fun debug(block: DebugOptions.() -> Unit) {
    debug.block()
  }

  override fun hashCode(): Int = super.hashCode()

  override fun equals(other: Any?): Boolean = super.equals(other)

  override fun toString(): String = "CompileOptions"
}
