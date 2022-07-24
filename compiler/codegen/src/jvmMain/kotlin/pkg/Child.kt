package org.plank.codegen.pkg

import java.io.File

fun File.child(name: String, recreate: Boolean = false, dir: Boolean = false): File {
  val file = resolve(name)

  if (recreate) {
    file.delete()

    if (dir) {
      file.mkdirs()
    } else {
      file.writeBytes(byteArrayOf())
    }
  }

  return file
}
