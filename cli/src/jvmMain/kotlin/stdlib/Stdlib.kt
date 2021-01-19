package com.lorenzoog.jplank.stdlib

import com.lorenzoog.jplank.element.PlankFile
import pw.binom.io.file.asBFile
import java.io.File

class Stdlib {
  val path: String? = System.getenv("PLANK_HOME")

  fun readStdlib(): List<PlankFile> {
    return File(path, "stdlib").listFiles().orEmpty().map {
      PlankFile.of(it.asBFile)
    }
  }
}
