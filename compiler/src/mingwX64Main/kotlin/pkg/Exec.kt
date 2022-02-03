package org.plank.compiler.pkg

import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix._pclose
import platform.posix._popen
import platform.posix.fgets

@Suppress("MaybeConst")
actual val pathSeparator: String = ";"

actual fun Command.exec(): String {
  val fp = _popen(toString(), "r") ?: error("Failed to run $executable")

  val stdout = buildString {
    val buf = ByteArray(1024)

    while (true) {
      val input = fgets(buf.refTo(0), buf.size, fp) ?: break
      append(input.toKString())
    }
  }

  val status = _pclose(fp)
  if (status != 0) {
    throw CommandFailedException(toString(), status)
  }

  return stdout
}
