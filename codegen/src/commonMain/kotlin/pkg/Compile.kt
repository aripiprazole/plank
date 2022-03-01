package org.plank.codegen.pkg

import org.plank.analyzer.element.ResolvedPlankFile
import org.plank.analyzer.pretty
import org.plank.analyzer.resolver.FileScope
import org.plank.analyzer.resolver.ModuleTree
import org.plank.codegen.DebugOptions
import org.plank.codegen.Entrypoint
import org.plank.codegen.intrinsics.DefaultIntrinsics
import org.plank.codegen.intrinsics.Intrinsics
import org.plank.codegen.scope.ScopeContext
import org.plank.codegen.scope.createFileContext
import org.plank.llvm4k.Context
import org.plank.llvm4k.Module
import org.plank.shared.depthFirstSearch
import org.plank.syntax.element.PlankFile
import org.plank.syntax.message.CompilerLogger

fun compile(
  plainMain: PlankFile,
  analyze: (PlankFile, ModuleTree) -> ResolvedPlankFile,
  debug: DebugOptions,
  tree: ModuleTree = ModuleTree(),
  logger: CompilerLogger = CompilerLogger(),
  intrinsics: Intrinsics = DefaultIntrinsics,
): Module {
  val main = analyze(plainMain, tree).check()

  if (debug.prettyDebug) {
    logger.debug("Pretty dump:")
    logger.debug(main.pretty())
    logger.debug()
  }

  val llvm = Context()
  val context = ScopeContext(llvm, main, debug).copy(scope = "Global").apply {
    addIntrinsics(intrinsics)
  }

  tree.dependencies
    .depthFirstSearch(main.module)
    .asSequence()
    .mapNotNull(tree::findModule)
    .map(org.plank.analyzer.resolver.Module::scope)
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

  if (checkViolations.isNotEmpty()) {
    throw AnalyzerError(checkViolations.toSet())
  }
}
