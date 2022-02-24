package org.plank.analyzer

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import platform.posix.sprintf

actual fun format(format: String, vararg args: Any?): String {
  return memScoped {
    val res = alloc<ByteVar>()
    sprintf(res.ptr, format, *args.toList().toTypedArray())
    res.ptr.toKString()
  }
}
