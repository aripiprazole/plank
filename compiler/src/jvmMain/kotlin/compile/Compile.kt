package com.gabrielleeg1.plank.compiler.compile

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.gabrielleeg1.plank.analyzer.FileScope
import com.gabrielleeg1.plank.analyzer.ModuleTree
import com.gabrielleeg1.plank.analyzer.element.ResolvedPlankFile
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
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
): Either<CompilerError, Module> {
  val main = analyze(plainMain, tree).check()

  if (debug.resolvedAstDebug) {
    logger.severe("Typed AST:")
    logger.severe(main.dumpTree())
    logger.severe()
  }

  val module = Module(LLVMModuleCreateWithName(main.module.text))
  val context = CompilerContext(debug.compilationDebug, module, main).copy(contextName = "Global")

  val violations = tree.dependencies
    .depthFirstSearch(main.module)
    .asSequence()
    .mapNotNull(tree::findModule)
    .map(com.gabrielleeg1.plank.analyzer.Module::scope)
    .filterIsInstance<FileScope>()
    .map(FileScope::file)
    .toList()
    .asReversed() // reverse order
    .map { analyze(it, tree).check() }
    .flatMap { plankModule ->
      context
        .createFileScope(plankModule)
        .also(context::addModule)
        .run {
          val instructions = plankModule.program.map { it.codegen() }

          if (currentFile.module == main.module) { // FIXME: running twice the type check
            instructions + EntryPoint().codegen()
          } else {
            instructions
          }
        }
    }
    .filterIsInstance<Either.Left<CodegenViolation>>()
    .map { it.value }
    .ifEmpty { return module.right() }

  return CompilerError(module, violations).left()
}
