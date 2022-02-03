package org.plank.compiler.pkg

import org.plank.analyzer.FileScope
import org.plank.analyzer.ModuleTree
import org.plank.analyzer.element.ResolvedPlankFile
import org.plank.compiler.Entrypoint
import org.plank.compiler.ScopeContext
import org.plank.compiler.createFileContext
import org.plank.compiler.intrinsics.DefaultIntrinsics
import org.plank.compiler.intrinsics.Intrinsics
import org.plank.grammar.debug.dumpTree
import org.plank.grammar.element.PlankFile
import org.plank.grammar.message.CompilerLogger
import org.plank.grammar.message.SimpleCompilerLogger
import org.plank.llvm4k.Context
import org.plank.llvm4k.Module
import org.plank.shared.depthFirstSearch

fun compile(
  plainMain: PlankFile,
  analyze: (PlankFile, ModuleTree) -> ResolvedPlankFile,
  debug: DebugOptions,
  tree: ModuleTree = ModuleTree(),
  logger: CompilerLogger = SimpleCompilerLogger(),
  intrinsics: Intrinsics = DefaultIntrinsics,
): Module {
  val main = analyze(plainMain, tree).check()

  if (debug.resolvedAstDebug) {
    logger.debug("Typed AST:")
    logger.debug(main.dumpTree())
    logger.debug()
  }

  val llvm = Context()
  val context = ScopeContext(llvm, main, debug.compilationDebug).copy(scope = "Global").apply {
    addIntrinsics(intrinsics)
  }

  tree.dependencies
    .depthFirstSearch(main.module)
    .asSequence()
    .mapNotNull(tree::findModule)
    .map(org.plank.analyzer.Module::scope)
    .filterIsInstance<FileScope>()
    .map(FileScope::file)
    .toList()
    .asReversed() // reverse order
    .map { if (it.moduleName == plainMain.moduleName) main else analyze(it, tree).check() }
    .flatMap { plankModule ->
      context.createFileContext(plankModule).also(context::addModule).run {
        val instructions = plankModule.program.map { it.codegen() }

        if (file == main) {
          instructions + Entrypoint().codegen()
        } else {
          instructions
        }
      }
    }

  return context.currentModule
}

private fun ResolvedPlankFile.check(): ResolvedPlankFile = apply {
  if (syntaxViolations.isNotEmpty()) {
    throw SyntaxError(syntaxViolations.toSet())
  }

  if (bindingViolations.isNotEmpty()) {
    throw AnalyzerError(bindingViolations.toSet())
  }
}
