package com.lorenzoog.plank.compiler

import com.lorenzoog.plank.analyzer.BindingContext
import com.lorenzoog.plank.analyzer.FileScope
import com.lorenzoog.plank.analyzer.ModuleTree
import com.lorenzoog.plank.compiler.instructions.EntryPoint
import com.lorenzoog.plank.grammar.element.PlankFile
import com.lorenzoog.plank.grammar.element.visit
import com.lorenzoog.plank.shared.depthFirstSearch
import org.bytedeco.llvm.global.LLVM
import org.bytedeco.llvm.global.LLVM.LLVMInitializeNativeAsmParser
import org.bytedeco.llvm.global.LLVM.LLVMInitializeNativeAsmPrinter
import org.bytedeco.llvm.global.LLVM.LLVMInitializeNativeDisassembler
import org.bytedeco.llvm.global.LLVM.LLVMInitializeNativeTarget
import org.bytedeco.llvm.global.LLVM.LLVMLinkInMCJIT
import org.llvm4j.llvm4j.Module
import org.llvm4j.llvm4j.Value
import pw.binom.io.Closeable

class PlankLLVM(
  private val tree: ModuleTree,
  private val bindingContext: BindingContext
) : Closeable {
  lateinit var context: PlankContext
    private set

  lateinit var module: Module
    private set

  private val instructionMapper = InstructionMapper(TypeMapper(), bindingContext)

  fun initialize(file: PlankFile) {
    LLVMLinkInMCJIT()
    LLVMInitializeNativeAsmPrinter()
    LLVMInitializeNativeAsmParser()
    LLVMInitializeNativeDisassembler()
    LLVMInitializeNativeTarget()

    module = Module(LLVM.LLVMModuleCreateWithName(file.module))

    context = PlankContext
      .of(file, instructionMapper, bindingContext, module)
      .copy(moduleName = "Global")
  }

  fun compile(main: PlankFile): List<Value> {
    return tree.dependencies
      .depthFirstSearch(main.module)
      .asSequence()
      .mapNotNull(tree::findModule)
      .map(com.lorenzoog.plank.analyzer.Module::scope)
      .filterIsInstance<FileScope>()
      .map(FileScope::file)
      .toList()
      .asReversed() // reverse order
      .flatMap { module ->
        val fileContext = context
          .createFileScope(module)
          .also(context::addModule)

        instructionMapper.visit(module.program).map {
          it.codegen(fileContext)
        }
      }
      .filterNotNull()
      .plus(EntryPoint().codegen(context))
  }

  override fun close() {
  }
}
