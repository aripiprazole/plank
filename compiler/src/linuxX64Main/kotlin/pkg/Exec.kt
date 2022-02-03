package org.plank.compiler.pkg

import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen

@Suppress("MaybeConst")
actual val pathSeparator: String = ":"

actual fun Command.exec(): String {
  val fp = popen(toString(), "r") ?: error("Failed to run $executable")

  val stdout = buildString {
    val buf = ByteArray(1024)

    while (true) {
      val input = fgets(buf.refTo(0), buf.size, fp) ?: break
      append(input.toKString())
    }
  }

  val status = pclose(fp)
  if (status != 0) {
    throw CommandFailedException(toString(), status)
  }

  return stdout
}
