package com.gabrielleeg1.plank.cli.utils

import pw.binom.io.file.File
import pw.binom.io.file.binom
import pw.binom.io.file.java
import pw.binom.io.file.mkdirs
import java.io.File as JFile
import java.nio.file.Path
import java.nio.file.Paths

val currentFile: File
  get() = Paths.get("").toAbsolutePath().toFile().binom

val File.children get() = java.listFiles().orEmpty().map(JFile::binom)

fun File.child(name: String, recreate: Boolean = false, dir: Boolean = false): File {
  val file = File(this, name)

  if (recreate) {
    file.delete()

    if (dir) {
      file.mkdirs()
    } else {
      file.java.createNewFile()
    }
  }

  return file
}

fun Path.asFile(): File {
  return toFile().binom
}

fun File.getRelativePath(child: File): String {
  return java.toRelativeString(child.java.absoluteFile)
}

fun Process.printOutput() {
  inputStream.bufferedReader().lineSequence().forEach {
    println(it)
  }

  errorStream.bufferedReader().lineSequence().forEach {
    println(it)
  }
}
