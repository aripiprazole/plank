package com.gabrielleeg1.plank.compiler.compile

import com.gabrielleeg1.plank.analyzer.ModuleTree
import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.grammar.message.CompilerLogger
import java.io.File
import java.nio.file.Paths

data class Package(
  val name: String,
  val main: PlankFile,
  val options: CompileOptions,
  val kind: Kind,
  val include: List<PlankFile> = emptyList(),
) {
  val logger: CompilerLogger get() = options.logger

  constructor(text: String, home: File, options: CompileOptions.() -> Unit = {}) : this(
    name = text,
    kind = Kind.Binary,
    main = PlankFile.of(text, debug = true),
    options = CompileOptions(home).apply(options),
  )

  constructor(text: String, options: CompileOptions.() -> Unit = {}) : this(
    name = text,
    kind = Kind.Binary,
    main = PlankFile.of(text, debug = true),
    options = CompileOptions(Paths.get(".").toAbsolutePath().toFile()).apply(options),
  )

  enum class Kind { Binary, Library }

  val tree = ModuleTree(include + main + include)
}
