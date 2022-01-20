package com.gabrielleeg1.plank.compiler.builder

import com.gabrielleeg1.plank.compiler.CompilerContext
import org.llvm4j.llvm4j.PointerType
import org.llvm4j.llvm4j.Type

fun CompilerContext.pointerType(type: Type): PointerType {
  return context.getPointerType(type).unwrap()
}
