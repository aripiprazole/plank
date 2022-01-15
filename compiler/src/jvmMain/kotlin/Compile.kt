package com.gabrielleeg1.plank.compiler

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.gabrielleeg1.plank.analyzer.FileScope
import com.gabrielleeg1.plank.analyzer.ModuleTree
import com.gabrielleeg1.plank.analyzer.element.ResolvedPlankFile
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
import com.gabrielleeg1.plank.compiler.instructions.EntryPoint
import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.shared.depthFirstSearch
import org.bytedeco.llvm.global.LLVM.LLVMModuleCreateWithName
import org.llvm4j.llvm4j.Module

data class CompilerError(val module: Module, val violations: List<CodegenViolation>)

fun compile(
  plainMain: PlankFile,
  analyze: (PlankFile, ModuleTree) -> ResolvedPlankFile,
  tree: ModuleTree = ModuleTree(),
  debug: Boolean = false,
): Either<CompilerError, Module> {
  val main = analyze(plainMain, tree)

  val module = Module(LLVMModuleCreateWithName(main.module.text))
  val context = CompilerContext(debug, module, main).copy(moduleName = "Global")

  val violations = tree.dependencies
    .depthFirstSearch(main.module)
    .asSequence()
    .mapNotNull(tree::findModule)
    .map(com.gabrielleeg1.plank.analyzer.Module::scope)
    .filterIsInstance<FileScope>()
    .map(FileScope::file)
    .toList()
    .asReversed() // reverse order
    .map { analyze(it, tree) }
    .flatMap { plankModule ->
      context
        .createFileScope(plankModule)
        .also(context::addModule)
        .run {
          val instructions = plankModule.program.map { it.toInstruction().codegen() }

          if (currentFile == main) {
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
