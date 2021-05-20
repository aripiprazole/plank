package com.lorenzoog.plank.cli.compiler

import com.lorenzoog.plank.cli.utils.child
import com.lorenzoog.plank.cli.utils.children
import com.lorenzoog.plank.grammar.element.PlankFile
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createTempDirectory
import pw.binom.io.file.File
import pw.binom.io.file.asBFile

@ExperimentalPathApi
class CompilerOptions(plankHome: File) {
  var debug = false
  var emitIR = false

  var make = "make"
  var linker = "clang++"
  var output = File("main")

  var dist = createTempDirectory().toFile().asBFile

  val objects by lazy { dist.child("objects", recreate = true, dir = true) }
  val ir by lazy { dist.child("ir", recreate = true, dir = true) }

  /** TODO: use a package manager */
  val stdlib = plankHome.child("stdlib").children
    .filter { it.path.endsWith(".plank") }
    .map { PlankFile.of(it) }

  /** TODO: remove ffi from stdlib */
  var runtime = plankHome.child("runtime")
}
