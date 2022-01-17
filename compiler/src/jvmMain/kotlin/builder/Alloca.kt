package com.gabrielleeg1.plank.compiler.builder

import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildAlloca
import com.gabrielleeg1.plank.compiler.buildStore
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.Value

fun CompilerContext.alloca(value: Value, name: String = "alloca.tmp"): AllocaInstruction =
  buildAlloca(value.getType(), name).also {
    buildStore(it, value)
  }
