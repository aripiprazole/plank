package org.plank.codegen.pkg

import kotlin.system.getTimeMillis

actual fun generateTempDirectoryName(name: String): String {
  return "$name-${getTimeMillis()}"
}
