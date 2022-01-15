package com.gabrielleeg1.plank.compiler.compile

import com.gabrielleeg1.plank.analyzer.ModuleTree
import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.grammar.message.CompilerLogger
import com.gabrielleeg1.plank.grammar.message.SimpleCompilerLogger
import java.nio.file.Paths

data class Package(
  val name: String,
  val main: PlankFile,
  val options: CompileOptions,
  val kind: Kind,
  val logger: CompilerLogger,
  val include: List<PlankFile> = emptyList(),
) {
  constructor(text: String, options: CompileOptions.() -> Unit = {}) : this(
    name = text,
    kind = Kind.Binary,
    main = PlankFile.of(text),
    options = CompileOptions(Paths.get("").toAbsolutePath().toFile()).apply(options),
    logger = SimpleCompilerLogger(),
  )

  enum class Kind { Binary, Library }

  val tree = ModuleTree(include + main + include)
}
