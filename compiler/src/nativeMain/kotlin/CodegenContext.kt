package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.element.ResolvedPlankElement
import com.gabrielleeg1.plank.analyzer.element.ResolvedPlankFile
import com.gabrielleeg1.plank.analyzer.element.ResolvedStmt
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.compiler.element.FunctionInst
import com.gabrielleeg1.plank.grammar.element.Location
import org.plank.llvm4k.Context
import org.plank.llvm4k.IRBuilder
import org.plank.llvm4k.Module
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.StructType
import org.plank.llvm4k.ir.Type
import org.plank.llvm4k.ir.Value
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed interface CodegenContext : Context, IRBuilder {
  val scope: String
  val currentModule: Module
  val file: ResolvedPlankFile
  val mapper: InstructionMapper
  val debug: Boolean // TODO: add more debug options
  val location: Location
  val enclosing: CodegenContext?

  fun expand(scope: ScopeContext)
  fun addModule(module: ScopeContext)

  fun addFunction(function: FunctionInst): Value
  fun addStruct(name: String, type: PlankType, struct: StructType)

  fun setSymbol(name: String, type: PlankType, variable: AllocaInst)

  fun findModule(name: String): ScopeContext?
  fun findStruct(name: String): StructType?
  fun findSymbol(name: String): AllocaInst
  fun findAlloca(name: String): AllocaInst?

  fun FunctionInst.access(): AllocaInst? = with(this@CodegenContext) { access() }

  fun PlankType.typegen(): Type = typegen(this)
  fun Collection<PlankType>.typegen(): List<Type> = map { it.typegen() }

  fun CodegenInstruction.codegen(): Value = with(this@CodegenContext) { codegen() }
  fun Collection<ResolvedPlankElement>.codegen(): List<Value> = map { it.codegen() }

  fun ResolvedPlankElement.codegen(): Value =
    DescriptorContext(this, scopeContext()).let { context ->
      when (context.descriptor) {
        is TypedExpr -> mapper.visit(context.descriptor).run { context.codegen() }
        is ResolvedStmt -> mapper.visit(context.descriptor).run { context.codegen() }
        else -> error("No available value mapping for ${this::class.simpleName}")
      }
    }
}

class ExecContext(
  override val enclosing: ScopeContext,
  val returnType: Type,
  val arguments: MutableMap<String, Value> = linkedMapOf(),
) : CodegenContext by enclosing

class DescriptorContext(
  val descriptor: ResolvedPlankElement,
  override val enclosing: ScopeContext,
) : CodegenContext by enclosing

data class ScopeContext(
  private val llvm: Context,
  private val irBuilder: IRBuilder,
  override val scope: String,
  override val currentModule: Module,
  override val file: ResolvedPlankFile,
  override val mapper: InstructionMapper = InstructionMapper,
  override val debug: Boolean = true,
  override val location: Location = file.location,
  override val enclosing: CodegenContext? = null,
) : IRBuilder by irBuilder, Context by llvm, CodegenContext {
  override fun expand(scope: ScopeContext) {
    TODO("Not yet implemented")
  }

  override fun addModule(module: ScopeContext) {
    TODO("Not yet implemented")
  }

  override fun addFunction(function: FunctionInst): Value {
    TODO("Not yet implemented")
  }

  override fun addStruct(name: String, type: PlankType, struct: StructType) {
    TODO("Not yet implemented")
  }

  override fun setSymbol(name: String, type: PlankType, variable: AllocaInst) {
    TODO("Not yet implemented")
  }

  override fun findModule(name: String): ScopeContext? {
    TODO("Not yet implemented")
  }

  override fun findStruct(name: String): StructType? {
    TODO("Not yet implemented")
  }

  override fun findSymbol(name: String): AllocaInst {
    TODO("Not yet implemented")
  }

  override fun findAlloca(name: String): AllocaInst? {
    TODO("Not yet implemented")
  }

  override fun toString(): String = "ScopeContext(scope=$scope, enclosing=$enclosing)"

  override fun close() {
    llvm.close()
    irBuilder.close()
  }
}

fun CodegenContext.scopeContext(): ScopeContext {
  return when (this) {
    is DescriptorContext -> enclosing
    is ExecContext -> enclosing
    is ScopeContext -> this
  }
}

@OptIn(ExperimentalContracts::class)
inline fun CodegenContext.createScopeContext(
  moduleName: String,
  builder: ScopeContext.() -> Unit = {}
): ScopeContext {
  contract {
    callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
  }

  return when (this) {
    is ScopeContext -> copy(enclosing = this, scope = moduleName).apply(builder)
    is DescriptorContext -> enclosing.copy(enclosing = enclosing, scope = moduleName).apply(builder)
    is ExecContext -> enclosing.copy(enclosing = enclosing, scope = moduleName).apply(builder)
  }
}
