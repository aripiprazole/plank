package org.plank.compiler.pkg

import pw.binom.io.file.File
import pw.binom.io.file.mkdirs
import pw.binom.io.file.rewrite
import kotlin.system.getTimeMillis

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
  return File.temporalDirectory!!.child("$name-${getTimeMillis()}", recreate = true, dir = true)
}
