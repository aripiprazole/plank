package com.gabrielleeg1.plank.cli

import com.gabrielleeg1.plank.analyzer.ModuleTree
import com.gabrielleeg1.plank.grammar.element.PlankFile

@ThreadLocal
object Builtins {
  private val StdIO = PlankFile.of(
    """
    module Std.IO;

    @intrinsic
    fun println(message: *Char): Void

    @intrinsic
    fun print(message: *Char): Void
    """.trimIndent(),
    module = "Std.IO",
  )

  val tree = ModuleTree(listOf(StdIO))
}
