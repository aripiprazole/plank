package com.lorenzoog.jplank.stdlib

import com.lorenzoog.jplank.element.PkFile
import pw.binom.io.file.asBFile
import java.io.File

class Stdlib {
  val path: String? = System.getenv("PLANK_HOME")

  fun readStdlib(): List<PkFile> {
    return File(path, "stdlib").listFiles().orEmpty().map {
      PkFile.of(it.asBFile)
    }
  }
}
