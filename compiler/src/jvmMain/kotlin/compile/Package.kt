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

  constructor(
    text: String,
    home: File = Paths.get(".").toAbsolutePath().toFile(),
    includeStd: Boolean = true,
    builder: CompileOptions.() -> Unit = {},
  ) : this(text, includeStd, CompileOptions(home).apply(builder))

  constructor(text: String, includeStd: Boolean, options: CompileOptions) : this(
    name = "Anonymous",
    kind = Kind.Binary,
    options = options,
    include = if (includeStd) options.stdlib else emptyList(),
    main = PlankFile.of(
      text,
      treeDebug = options.debug.treeDebug,
      parserDebug = options.debug.parserDebug,
      logger = options.logger,
    )
  )

  enum class Kind { Binary, Library }

  val tree = ModuleTree(include + main + include)
}
