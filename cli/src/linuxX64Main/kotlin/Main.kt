package com.gabrielleeg1.plank.cli

fun main(args: Array<String>) {
  llvm.LLVMInitializeNativeAsmPrinter()
  llvm.LLVMInitializeNativeAsmParser()

  PlankCLI().main(args)
}
