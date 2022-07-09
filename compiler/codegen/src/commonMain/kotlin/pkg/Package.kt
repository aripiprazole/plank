package org.plank.codegen.pkg

import okio.Path
import okio.Path.Companion.toPath
import org.plank.analyzer.resolver.ModuleTree
import org.plank.shared.readText
import org.plank.syntax.element.PlankFile
import org.plank.syntax.message.CompilerLogger

data class Package(
  val name: String,
  val main: PlankFile,
  val options: CompileOptions,
  val kind: Kind,
  val include: List<PlankFile> = emptyList(),
) : CompilerLogger by options.logger {
  val logger: CompilerLogger get() = options.logger

  constructor(
    file: Path,
    home: Path = ".".toPath(),
    includeStd: Boolean = true,
    builder: CompileOptions.() -> Unit = {},
  ) : this(file.readText(), includeStd, CompileOptions(home).apply(builder), file.toString())

  constructor(
    text: String,
    home: Path = ".".toPath(),
    includeStd: Boolean = true,
    builder: CompileOptions.() -> Unit = {},
  ) : this(text, includeStd, CompileOptions(home).apply(builder))

  constructor(
    text: String,
    includeStd: Boolean,
    options: CompileOptions,
    path: String = "anonymous",
  ) : this(
    name = "Anonymous",
    kind = Kind.Binary,
    options = options,
    include = if (includeStd) options.stdlib else emptyList(),
    main = PlankFile.of(
      text,
      path = path,
      treeDebug = options.debug.treeDebug,
      parserDebug = options.debug.parserDebug,
      logger = options.logger,
    )
  )

  enum class Kind { Binary, Library }

  val tree = ModuleTree.create(include + main + include)
}
