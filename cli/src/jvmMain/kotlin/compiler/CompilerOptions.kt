package com.lorenzoog.jplank.compiler

import com.lorenzoog.jplank.utils.child
import pw.binom.io.file.File

class CompilerOptions(root: File, plankHome: File) {
  var debug = false
  var emitIR = false

  var make = "make"
  var cmake = "cmake"
  var linker = "clang++"
  var output = root.child("main")

  var dist = root.child("dist", recreate = true, dir = true)

  val objects by lazy { dist.child("objects", recreate = true, dir = true) }
  val ir by lazy { dist.child("ir", recreate = true, dir = true) }

  /** TODO: remove ffi from stdlib */
  var stdlib = plankHome.child("runtime")

  /** TODO: remove ffi from stdlib */
  val stdlibTarget by lazy { dist.child("cmake", recreate = true, dir = true) }
}
