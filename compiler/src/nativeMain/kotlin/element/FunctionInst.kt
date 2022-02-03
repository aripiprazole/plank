package org.plank.compiler.element

import org.plank.analyzer.PlankType
import org.plank.analyzer.UnitType
import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.analyzer.element.ResolvedReturnStmt
import org.plank.compiler.CodegenContext
import org.plank.compiler.CodegenInstruction
import org.plank.compiler.ExecContext
import org.plank.compiler.alloca
import org.plank.compiler.codegenError
import org.plank.compiler.createUnit
import org.plank.grammar.element.Identifier
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
      setSymbol(name.text, type, alloca(argument, "parameter.${name.text}"))
    }
  }
