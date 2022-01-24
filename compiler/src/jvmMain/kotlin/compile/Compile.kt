package com.gabrielleeg1.plank.compiler.compile

import com.gabrielleeg1.plank.analyzer.FileScope
import com.gabrielleeg1.plank.analyzer.ModuleTree
import com.gabrielleeg1.plank.analyzer.element.ResolvedPlankFile
import com.gabrielleeg1.plank.compiler.ScopeContext
import com.gabrielleeg1.plank.compiler.instructions.EntryPoint
import com.gabrielleeg1.plank.grammar.debug.dumpTree
import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.grammar.message.CompilerLogger
import com.gabrielleeg1.plank.grammar.message.SimpleCompilerLogger
import com.gabrielleeg1.plank.shared.depthFirstSearch
import org.bytedeco.llvm.global.LLVM.LLVMModuleCreateWithName
import org.llvm4j.llvm4j.Module

private fun ResolvedPlankFile.check(): ResolvedPlankFile = apply {
  if (syntaxViolations.isNotEmpty()) {
    throw SyntaxError(syntaxViolations)
  }

  if (bindingViolations.isNotEmpty()) {
    throw BindingError(bindingViolations)
  }
}

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

  val module = Module(LLVMModuleCreateWithName(main.module.text))
  val context = ScopeContext(debug.compilationDebug, module, main).copy(name = "Global")

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
      context.createFileScope(plankModule).also(context::addModule).run {
        val instructions = plankModule.program.map { it.codegen() }

        if (file == main) {
          instructions + EntryPoint().codegen()
        } else {
          instructions
        }
      }
    }

  return module
}
