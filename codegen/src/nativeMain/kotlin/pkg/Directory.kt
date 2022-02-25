package org.plank.codegen.pkg

import pw.binom.io.file.File
import kotlin.system.getTimeMillis

actual fun createTempDirectory(name: String): File {
  return File.temporalDirectory!!.child(
    "$name-${getTimeMillis()}",
    recreate = true,
    dir = true
  )
}
