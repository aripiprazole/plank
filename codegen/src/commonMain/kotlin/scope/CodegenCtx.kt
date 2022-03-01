package org.plank.codegen.scope

import org.plank.analyzer.element.ResolvedPlankElement
import org.plank.analyzer.element.ResolvedPlankFile
import org.plank.analyzer.element.ResolvedStmt
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.ap
import org.plank.analyzer.infer.nullSubst
import org.plank.codegen.CodegenInstruction
import org.plank.codegen.DebugContext
import org.plank.codegen.element.FunctionSymbol
import org.plank.codegen.element.LazySymbol
import org.plank.codegen.element.Symbol
import org.plank.codegen.element.ValueSymbol
import org.plank.codegen.exprToInstruction
import org.plank.codegen.intrinsics.IntrinsicFunction
import org.plank.codegen.stmtToInstruction
import org.plank.codegen.typegen
import org.plank.llvm4k.Context
import org.plank.llvm4k.IRBuilder
import org.plank.llvm4k.Module
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.StructType
import org.plank.llvm4k.ir.Type
import org.plank.llvm4k.ir.User
import org.plank.llvm4k.ir.Value
import org.plank.syntax.element.Loc
import org.plank.syntax.element.QualifiedPath

sealed interface CodegenCtx : Context, IRBuilder {
  val scope: String
  val file: ResolvedPlankFile
  val debug: DebugContext
  val currentModule: Module
  val loc: Loc
  val path: QualifiedPath
  val enclosing: CodegenCtx?

  val subst: Subst get() = enclosing?.subst ?: nullSubst()

  val unit: StructType

  fun expand(scope: ScopeContext)
  fun addModule(module: ScopeContext)

  fun addFunction(function: FunctionSymbol, isGeneric: Boolean = false): Value
  fun addStruct(name: String, struct: Type)

  fun getSymbol(scope: CodegenCtx, name: String, subst: Subst = nullSubst()): User
  fun setSymbol(name: String, value: Symbol): Value

  fun setSymbol(name: String, type: Ty, variable: User): Value {
    return setSymbol(name, ValueSymbol(type, variable))
  }

  fun setSymbolLazy(name: String, type: Ty, lazyValue: CodegenCtx.() -> Value): Value {
    return setSymbol(name, LazySymbol(type, name, lazyValue))
  }

  fun findFunction(name: String): Symbol?
  fun findModule(name: String): ScopeContext?
  fun findStruct(name: String): Type?
  fun findAlloca(name: String, subst: Subst = nullSubst()): User?
  fun findIntrinsic(name: String): IntrinsicFunction?

  fun lazyLocal(name: String, builder: () -> AllocaInst?): AllocaInst?

  fun Symbol.access(subst: Subst = nullSubst()): User? = with(this@CodegenCtx) { access(subst) }

  fun Ty.typegen(): Type = typegen(this.ap(subst))
  fun Collection<Ty>.typegen(): List<Type> = map { it.typegen() }

  fun CodegenInstruction.codegen(): Value = with(this@CodegenCtx) { codegen() }
  fun Collection<ResolvedPlankElement>.codegen(): List<Value> = map { it.codegen() }

  fun ResolvedPlankElement.codegen(): Value =
    DescriptorContext(this, scopeContext()).let { context ->
      when (context.descriptor) {
        is TypedExpr -> exprToInstruction(context.descriptor).run { context.codegen() }
        is ResolvedStmt -> stmtToInstruction(context.descriptor).run { context.codegen() }
        else -> error("No available value mapping for ${this::class.simpleName}")
      }
    }
}
