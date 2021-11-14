package com.gabrielleeg1.plank.cli.pkg

import com.gabrielleeg1.plank.analyzer.ModuleTree
import com.gabrielleeg1.plank.cli.compiler.CompilerOptions
import com.gabrielleeg1.plank.grammar.element.PlankFile
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
