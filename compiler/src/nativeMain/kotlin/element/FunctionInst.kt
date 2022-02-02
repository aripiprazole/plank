package com.gabrielleeg1.plank.compiler.element

import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.UnitType
import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.analyzer.element.ResolvedReturnStmt
import com.gabrielleeg1.plank.compiler.CodegenContext
import com.gabrielleeg1.plank.compiler.CodegenInstruction
import com.gabrielleeg1.plank.compiler.ExecContext
import com.gabrielleeg1.plank.compiler.alloca
import com.gabrielleeg1.plank.compiler.codegenError
import com.gabrielleeg1.plank.compiler.createUnit
import com.gabrielleeg1.plank.grammar.element.Identifier
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.Argument

typealias GenerateBody = ExecContext.() -> Unit

sealed interface FunctionInst : CodegenInstruction {
  val name: String

  fun CodegenContext.access(): AllocaInst?
}

class BodyGenerator(private val descriptor: ResolvedFunDecl) : (ExecContext) -> Unit {
  override fun invoke(ctx: ExecContext): Unit = with(ctx) {
    descriptor.content.codegen()

    if (descriptor.type.actualReturnType != UnitType) return
    if (descriptor.content.filterIsInstance<ResolvedReturnStmt>().isNotEmpty()) return

    createRet(createUnit())
  }
}

fun ExecContext.generateParameter(parameters: Map<Identifier, PlankType>) =
  fun(index: Int, argument: Argument) {
    val (name, type) = parameters.entries.elementAtOrElse(index) {
      codegenError("Unresolved parameter `$index`")
    }

    argument.name = name.text

    arguments[name.text] = argument

    if (type.isNested) {
      setSymbol(name.text, type, AllocaInst(argument.ref))
    } else {
      setSymbol(name.text, type, alloca(argument, "parameter.$name"))
    }
  }
