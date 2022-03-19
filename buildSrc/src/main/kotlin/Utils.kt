package org.plank.build

import java.nio.file.Paths

fun String.absolutePath(): String {
  return Paths.get(this).toAbsolutePath().toString().replace("\n", "")
}
