package org.plank.shared

import okio.FileSystem

expect object Platform {
  val FileSystem: FileSystem
}
