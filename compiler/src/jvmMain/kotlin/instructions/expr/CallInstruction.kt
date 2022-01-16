package com.gabrielleeg1.plank.compiler.instructions.expr

import arrow.core.computations.either
import arrow.core.left
import com.gabrielleeg1.plank.analyzer.element.TypedCallExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.buildCall
import com.gabrielleeg1.plank.compiler.buildLoad
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedFunctionError
import com.gabrielleeg1.plank.compiler.unsafeCast
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.LoadInstruction

class CallInstruction(private val descriptor: TypedCallExpr) : CompilerInstruction() {
  override fun CompilerContext.codegen(): CodegenResult = either.eager {
    val function = when (val callee = descriptor.callee.toInstruction().codegen().bind()) {
      is Function -> callee
      is LoadInstruction -> callee.unsafeCast()
      is AllocaInstruction -> buildLoad(callee).unsafeCast()
      else -> {
        unresolvedFunctionError(descriptor.callee).left().bind<Function>()
      }
    }

    val arguments = descriptor.arguments.map { it.toInstruction().codegen().bind() }

    buildCall(function, arguments)
  }
}
