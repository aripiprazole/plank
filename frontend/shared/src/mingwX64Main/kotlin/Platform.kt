package org.plank.shared

import okio.FileSystem

actual object Platform {
  actual val FileSystem: FileSystem = okio.FileSystem.SYSTEM
}
