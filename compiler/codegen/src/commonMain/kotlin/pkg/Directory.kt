package org.plank.codegen.pkg

import okio.FileSystem
import okio.Path
import org.plank.shared.Platform

fun Path.child(name: String, recreate: Boolean = false, dir: Boolean = false): Path {
  val file = resolve(name)

  if (recreate) {
    Platform.FileSystem.delete(file)

    if (dir) {
      Platform.FileSystem.createDirectory(file)
    } else {
      Platform.FileSystem.write(file) {
        write(byteArrayOf())
      }
    }
  }

  return file
}

fun createTempDirectory(name: String): Path {
  return FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve(name).also { temp ->
    Platform.FileSystem.createDirectory(temp)
  }
}
