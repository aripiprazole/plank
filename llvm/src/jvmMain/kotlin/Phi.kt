package com.lorenzoog.jplank.compiler.llvm

import org.bytedeco.llvm.global.LLVM.LLVMAddIncoming
import org.bytedeco.llvm.global.LLVM.LLVMCountIncoming
import org.llvm4j.llvm4j.BasicBlock
import org.llvm4j.llvm4j.PhiInstruction
import org.llvm4j.llvm4j.Value

fun PhiInstruction.addIncoming(values: List<Value>, blocks: List<BasicBlock>) {
  LLVMAddIncoming(
    ref,
    values.map(Value::ref).asPointer(),
    blocks.map(BasicBlock::ref).asPointer(),
    LLVMCountIncoming(ref)
  )
}
