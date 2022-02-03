package org.plank.compiler.pkg

import org.plank.analyzer.ModuleTree
import org.plank.grammar.element.PlankFile
import org.plank.grammar.message.CompilerLogger
import pw.binom.io.file.File

data class Package(
  val name: String,
  val main: PlankFile,
  val options: CompileOptions,
  val kind: Kind,
  val include: List<PlankFile> = emptyList(),
) : CompilerLogger by options.logger {
  val logger: CompilerLogger get() = options.logger

  constructor(
    text: String,
    home: File = File("."),
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
