package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.BindingContext
import com.gabrielleeg1.plank.analyzer.FileScope
import com.gabrielleeg1.plank.analyzer.ModuleTree
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.EntryPoint
import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.shared.depthFirstSearch
import org.bytedeco.llvm.global.LLVM
import org.bytedeco.llvm.global.LLVM.LLVMInitializeNativeAsmParser
import org.bytedeco.llvm.global.LLVM.LLVMInitializeNativeAsmPrinter
import org.bytedeco.llvm.global.LLVM.LLVMInitializeNativeDisassembler
import org.bytedeco.llvm.global.LLVM.LLVMInitializeNativeTarget
import org.bytedeco.llvm.global.LLVM.LLVMLinkInMCJIT
import org.llvm4j.llvm4j.Module
import pw.binom.io.Closeable

class LlvmBackend(
  private val tree: ModuleTree,
  private val bindingContext: BindingContext
) : Closeable {
  lateinit var context: CompilerContext
    private set

  lateinit var module: Module
    private set

  fun initialize(file: PlankFile, debug: Boolean) {
    LLVMLinkInMCJIT()
    LLVMInitializeNativeAsmPrinter()
    LLVMInitializeNativeAsmParser()
    LLVMInitializeNativeDisassembler()
    LLVMInitializeNativeTarget()

    module = Module(LLVM.LLVMModuleCreateWithName(file.module))

    context = CompilerContext
      .of(file, bindingContext, module, debug)
      .copy(moduleName = "Global")
  }

  fun compile(main: PlankFile): List<CodegenResult> {
    return tree.dependencies
      .depthFirstSearch(main.module)
      .asSequence()
      .mapNotNull(tree::findModule)
      .map(com.gabrielleeg1.plank.analyzer.Module::scope)
      .filterIsInstance<FileScope>()
      .map(FileScope::file)
      .toList()
      .asReversed() // reverse order
      .flatMap { module ->
        context
          .createFileScope(module)
          .also(context::addModule)
          .run {
            val instructions = module.program.map { it.toInstruction().codegen() }

            if (currentFile == main) {
              instructions + EntryPoint().codegen()
            } else {
              instructions
            }
          }
      }
  }

  override fun close() {
  }
}
