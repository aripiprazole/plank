package org.plank.cli

fun main(args: Array<String>) {
  llvm.LLVMInitializeNativeAsmPrinter()
  llvm.LLVMInitializeNativeAsmParser()

  PlankCLI().main(args)
}
