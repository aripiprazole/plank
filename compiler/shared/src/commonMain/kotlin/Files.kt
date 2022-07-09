package org.plank.shared

import okio.Path

val Path.nameWithoutExtension: String get() = name.split(".").first()

val Path.extension: String get() = name.split(".")[1]

fun Path.readText(): String {
  return Platform.FileSystem.read(this) {
    readByteString().utf8()
  }
}

fun Path.exists(): Boolean {
  return Platform.FileSystem.exists(this)
}

fun Path.list(): List<Path> {
  return Platform.FileSystem.list(this)
}

fun Path.rewrite(text: String) {
  Platform.FileSystem.write(this) {
    writeUtf8(text)
  }
}
