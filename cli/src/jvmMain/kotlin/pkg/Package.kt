package com.lorenzoog.plank.cli.pkg

import com.lorenzoog.plank.analyzer.ModuleTree
import com.lorenzoog.plank.cli.compiler.CompilerOptions
import com.lorenzoog.plank.grammar.element.PlankFile
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
