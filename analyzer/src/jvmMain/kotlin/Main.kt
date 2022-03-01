package org.plank.analyzer

import org.plank.analyzer.checker.typeCheck
import org.plank.analyzer.resolver.ModuleTree
import org.plank.analyzer.resolver.resolveImports
import org.plank.syntax.debug.dumpTree
import org.plank.syntax.element.PlankFile
import org.plank.syntax.message.CompilerLogger

private val ioPlank = PlankFile.of(
  """
  module Std.IO;

  @intrinsic
  fun println(message: *Char);

  @intrinsic
  fun panic(message: *Char) -> c;
  """.trimIndent(),
)

private val maybePlank = PlankFile.of(
  """
  module Std.Maybe;

  use Std.IO;

  enum Maybe[a] {
    Just(a),
    Nothing
  }

  fun unwrap(m: Maybe[a]) -> a = match m {
    Just(x) => x,
    Nothing => panic("unwrap: Nothing")
  }
  """.trimIndent(),
)

private val mainPlank = PlankFile.of(
  """
  module Main;

  use Std.IO;
  use Std.Maybe;

  fun main() {
    let unwrap = Std.Maybe.unwrap;

    unwrap(Just(10));
  }
  """.trimIndent(),
)

typealias Transformer = (PlankFile, ModuleTree) -> PlankFile

val logger = CompilerLogger(debug = true, verbose = true)

fun main() {
  val tree = ModuleTree.create(maybePlank, ioPlank)
  val file = resolveImports(mainPlank, tree).typeCheck(logger)

  file.checkViolations.forEach {
    it.render(logger)
  }

  file.dependencies.forEach {
    println(it.dumpTree())
    println()
  }

  println(file.dumpTree())
  println()
}

fun analyze(f: PlankFile, tree: ModuleTree, vararg fns: Transformer): PlankFile {
  return fns.fold(f) { acc, next -> next(acc, tree) }
}
