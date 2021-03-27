package com.lorenzoog.plank.cli.utils

import java.nio.file.Path
import java.nio.file.Paths
import pw.binom.io.file.File
import pw.binom.io.file.asBFile
import pw.binom.io.file.asJFile
import pw.binom.io.file.mkdirs

val currentFile: File
  get() = Paths.get("").toAbsolutePath().toFile().asBFile

val File.children get() = asJFile.listFiles().orEmpty().map(java.io.File::asBFile)

fun File.child(name: String, recreate: Boolean = false, dir: Boolean = false): File {
  val file = File(this, name)

  if (recreate) {
    file.delete()

    if (dir) {
      file.mkdirs()
    } else {
      file.asJFile.createNewFile()
    }
  }

  return file
}

fun Path.asFile(): File {
  return toFile().asBFile
}

fun File.getRelativePath(child: File): String {
  return asJFile.toRelativeString(child.asJFile.absoluteFile)
}

fun Process.printOutput() {
  inputStream.bufferedReader().lineSequence().forEach {
    println(it)
  }

  errorStream.bufferedReader().lineSequence().forEach {
    println(it)
  }
}
