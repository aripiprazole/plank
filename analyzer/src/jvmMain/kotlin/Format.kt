package org.plank.analyzer

actual fun format(format: String, vararg args: Any?): String {
  return String.format(format, *args)
}
