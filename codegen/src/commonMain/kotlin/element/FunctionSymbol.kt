package org.plank.codegen.element

import org.plank.analyzer.element.ResolvedCodeBody
import org.plank.analyzer.element.ResolvedExprBody
import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.analyzer.element.ResolvedNoBody
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.unitTy
import org.plank.codegen.ExecContext
import org.plank.codegen.alloca
import org.plank.codegen.codegenError
import org.plank.codegen.createUnit
import org.plank.llvm4k.ir.Argument
import org.plank.syntax.element.Identifier

typealias GenerateBody = ExecContext.() -> Unit

sealed interface FunctionSymbol : Symbol {
  val name: String
}

class BodyGenerator(private val descriptor: ResolvedFunDecl) : (ExecContext) -> Unit {
  override fun invoke(ctx: ExecContext): Unit = with(ctx) {
    when (val body = descriptor.body) {
      is ResolvedNoBody -> {}
      is ResolvedExprBody -> createRet(body.expr.codegen())
      is ResolvedCodeBody -> {
        body.stmts.codegen()

        body.value?.let { returned ->
          createRet(returned.codegen())
        }

        if (descriptor.returnTy != unitTy) return
        if (body.hasReturnedUnit) return

        createRet(createUnit())
      }
    }
  }
}

fun ExecContext.generateParameter(parameters: Map<Identifier, Ty>) =
  fun(index: Int, argument: Argument) {
    val (name, type) = parameters.entries.elementAtOrElse(index) {
      codegenError("Unresolved parameter `$index`")
    }

    argument.name = name.text

    arguments[name.text] = argument

    setSymbol(name.text, type, alloca(argument, "parameter.${name.text}"))
  }
