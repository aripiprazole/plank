@file:JvmName("DirectoryJvm")

package org.plank.codegen.pkg

import pw.binom.io.file.File
import pw.binom.io.file.binom

actual fun createTempDirectory(name: String): File {
  return kotlin.io.path.createTempDirectory(name).toFile().binom
}
