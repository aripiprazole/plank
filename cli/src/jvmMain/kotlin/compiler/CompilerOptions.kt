package com.gabrielleeg1.plank.cli.compiler

import com.gabrielleeg1.plank.cli.utils.child
import com.gabrielleeg1.plank.cli.utils.children
import com.gabrielleeg1.plank.grammar.element.PlankFile
import pw.binom.io.file.File
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createTempDirectory
import pw.binom.io.file.binom

@ExperimentalPathApi
class CompilerOptions(plankHome: File) {
  var debug = false
  var emitIR = false

  var make = "make"
  var linker = "clang++"
  var output = File("main")

  var dist = createTempDirectory().toFile().binom

  val objects by lazy { dist.child("objects", recreate = true, dir = true) }
  val ir by lazy { dist.child("ir", recreate = true, dir = true) }

  /** TODO: use a package manager */
  val stdlib = plankHome.child("stdlib").children
    .filter { it.path.endsWith(".plank") }
    .map { PlankFile.of(it) }

  /** TODO: remove ffi from stdlib */
  var runtime = plankHome.child("runtime")
}
