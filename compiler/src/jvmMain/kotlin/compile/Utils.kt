package com.gabrielleeg1.plank.compiler.compile

import java.io.File

val File.children get() = listFiles().orEmpty()

fun File.child(name: String, recreate: Boolean = false, dir: Boolean = false): File {
  val file = resolve(name)

  if (recreate) {
    file.delete()

    if (dir) {
      file.mkdirs()
    } else {
      file.createNewFile()
    }
  }

  return file
}
