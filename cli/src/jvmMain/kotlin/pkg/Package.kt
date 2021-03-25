package com.lorenzoog.jplank.pkg

import com.lorenzoog.jplank.analyzer.ModuleTree
import com.lorenzoog.jplank.compiler.CompilerOptions
import com.lorenzoog.jplank.element.PlankFile
import com.lorenzoog.jplank.utils.children
import com.lorenzoog.jplank.utils.getRelativePath
import pw.binom.io.file.File

data class Package(
  val name: String,
  val prefix: String?,
  val main: String,
  val root: File,
  val options: CompilerOptions,
  val kind: Kind,
) {
  enum class Kind { Binary, Library }

  val tree = ModuleTree(
    root.children
      .map(PlankFile.Companion::of)
      .map { file ->
        val module = root
          .getRelativePath(File(file.path))
          .replace(File.SEPARATOR, '.')
          .let { module ->
            if (prefix != null) "$prefix.$module" else module
          }

        file.copy(module = module)
      }
  )
}
