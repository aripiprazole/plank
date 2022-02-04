package org.plank.codegen.element

import org.plank.analyzer.PlankType
import org.plank.analyzer.UnitType
import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.analyzer.element.ResolvedReturnStmt
import org.plank.codegen.ExecContext
import org.plank.codegen.alloca
import org.plank.codegen.codegenError
import org.plank.codegen.createUnit
import org.plank.codegen.unsafeAlloca
import org.plank.llvm4k.ir.Argument
import org.plank.syntax.element.Identifier

typealias GenerateBody = ExecContext.() -> Unit

sealed interface FunctionInst : ValueInst {
  val name: String
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
      setSymbol(name.text, type, unsafeAlloca(argument))
    } else {
      setSymbol(name.text, type, alloca(argument, "parameter.${name.text}"))
    }
  }
