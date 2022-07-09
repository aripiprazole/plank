package org.plank.shared

import kotlinx.cinterop.toKString
import platform.posix.getenv

actual object Environment {
  actual operator fun get(name: String): String? = getenv(name)?.toKString()
}
