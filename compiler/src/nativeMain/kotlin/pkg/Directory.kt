package com.gabrielleeg1.plank.compiler.pkg

import pw.binom.io.file.File
import pw.binom.io.file.mkdirs
import pw.binom.io.file.rewrite

fun File.child(name: String, recreate: Boolean = false, dir: Boolean = false): File {
  val file = File(this, name)

  if (recreate) {
    file.delete()

    if (dir) {
      file.mkdirs()
    } else {
      file.rewrite("")
    }
  }

  return file
}

fun createTempDirectory(name: String): File {
  TODO()
}
