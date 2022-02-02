package com.gabrielleeg1.plank.compiler.pkg

import com.gabrielleeg1.plank.analyzer.FileScope
import com.gabrielleeg1.plank.analyzer.ModuleTree
import com.gabrielleeg1.plank.analyzer.element.ResolvedPlankFile
import com.gabrielleeg1.plank.compiler.Entrypoint
import com.gabrielleeg1.plank.compiler.ScopeContext
import com.gabrielleeg1.plank.compiler.createFileContext
import com.gabrielleeg1.plank.grammar.debug.dumpTree
import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.grammar.message.CompilerLogger
import com.gabrielleeg1.plank.grammar.message.SimpleCompilerLogger
import com.gabrielleeg1.plank.shared.depthFirstSearch
import org.plank.llvm4k.Context
import org.plank.llvm4k.Module

fun compile(
  plainMain: PlankFile,
  analyze: (PlankFile, ModuleTree) -> ResolvedPlankFile,
  debug: DebugOptions,
  tree: ModuleTree = ModuleTree(),
  logger: CompilerLogger = SimpleCompilerLogger(),
): Module {
  val main = analyze(plainMain, tree).check()

  if (debug.resolvedAstDebug) {
    logger.debug("Typed AST:")
    logger.debug(main.dumpTree())
    logger.debug()
  }

  val llvm = Context()
  val context = ScopeContext(llvm, main, debug.compilationDebug).copy(scope = "Global")

  tree.dependencies
    .depthFirstSearch(main.module)
    .asSequence()
    .mapNotNull(tree::findModule)
    .map(com.gabrielleeg1.plank.analyzer.Module::scope)
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
