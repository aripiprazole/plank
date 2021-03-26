package com.lorenzoog.jplank.pkg

import com.lorenzoog.jplank.analyzer.ModuleTree
import com.lorenzoog.jplank.compiler.CompilerOptions
import com.lorenzoog.jplank.element.PlankFile
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
data class Package(
  val name: String,
  val main: PlankFile,
  val options: CompilerOptions,
  val kind: Kind,
  val include: List<PlankFile> = emptyList()
) {
  enum class Kind { Binary, Library }

  val tree = ModuleTree(include + main + include)
}
