package com.lorenzoog.jplank.linker

import pw.binom.io.file.File

interface Linker {
  val opts: LinkerOpts

  fun generateStdlibObjects(): Boolean

  fun generateObject(file: File): Pair<Int, File>

  fun linkObjects(targets: List<File>, name: String): Int
}
