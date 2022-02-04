@file:JvmName("CommandKtJvm")

package org.plank.codegen.pkg

import java.io.File

actual val pathSeparator: String = File.pathSeparator

actual fun Command.exec(): String {
  val process = Runtime.getRuntime().exec(toString())

  val text = process.inputStream.bufferedReader().readText()
  val status = process.waitFor()
  if (status != 0) {
    throw CommandFailedException(toString(), status)
  }

  return text
}
