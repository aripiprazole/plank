package com.gabrielleeg1.plank.compiler

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.identity
import arrow.core.traverseEither
import com.gabrielleeg1.plank.analyzer.FileScope
import com.gabrielleeg1.plank.analyzer.ModuleTree
import com.gabrielleeg1.plank.analyzer.element.ResolvedPlankFile
import com.gabrielleeg1.plank.compiler.instructions.CodegenError
import com.gabrielleeg1.plank.compiler.instructions.EntryPoint
import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.shared.depthFirstSearch
import org.bytedeco.llvm.global.LLVM.LLVMModuleCreateWithName
import org.llvm4j.llvm4j.Module

fun compile(
  plainMain: PlankFile,
  analyze: (PlankFile, ModuleTree) -> ResolvedPlankFile,
  tree: ModuleTree = ModuleTree(),
  debug: Boolean = false,
): Either<CodegenError, Module> = either.eager {
  val main = analyze(plainMain, tree)

  val module = Module(LLVMModuleCreateWithName(main.module.text))
  val context = CompilerContext(debug, module, main).copy(moduleName = "Global")

  tree.dependencies
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
    .traverseEither(::identity)
    .bind()

  module
}
