package com.lorenzoog.jplank.pkg

import com.lorenzoog.jplank.analyzer.ModuleTree
import com.lorenzoog.jplank.compiler.CompilerOptions
import com.lorenzoog.jplank.element.PlankFile
import pw.binom.io.file.File

data class Package(
  val name: String,
  val main: PlankFile,
  val root: File,
  val options: CompilerOptions,
  val kind: Kind,
  val include: List<PlankFile> = emptyList()
) {
  enum class Kind { Binary, Library }

  val tree = ModuleTree(include + main + include)
}
