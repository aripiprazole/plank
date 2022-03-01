package org.plank.codegen

import org.plank.analyzer.element.ResolvedPlankElement
import org.plank.analyzer.element.ResolvedPlankFile
import org.plank.analyzer.element.ResolvedStmt
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.Ty
import org.plank.analyzer.infer.ap
import org.plank.analyzer.infer.nullSubst
import org.plank.codegen.element.FunctionSymbol
import org.plank.codegen.element.LazySymbol
import org.plank.codegen.element.RankedSymbol
import org.plank.codegen.element.Symbol
import org.plank.codegen.element.ValueSymbol
import org.plank.codegen.intrinsics.IntrinsicFunction
import org.plank.codegen.intrinsics.Intrinsics
import org.plank.llvm4k.Context
import org.plank.llvm4k.IRBuilder
import org.plank.llvm4k.Module
import org.plank.llvm4k.ir.AllocaInst
import org.plank.llvm4k.ir.Function
import org.plank.llvm4k.ir.StructType
import org.plank.llvm4k.ir.Type
import org.plank.llvm4k.ir.User
import org.plank.llvm4k.ir.Value
import org.plank.syntax.element.Loc
import org.plank.syntax.element.QualifiedPath
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed interface CodegenContext : Context, IRBuilder {
  val scope: String
  val file: ResolvedPlankFile
  val debug: DebugContext
  val currentModule: Module
  val loc: Loc
  val path: QualifiedPath
  val enclosing: CodegenContext?

  val subst: Subst get() = enclosing?.subst ?: nullSubst()

  val unit: StructType

  fun expand(scope: ScopeContext)
  fun addModule(module: ScopeContext)

  fun addFunction(function: FunctionSymbol, isGeneric: Boolean = false): Value
  fun addStruct(name: String, struct: Type)

  fun getSymbol(scope: CodegenContext, name: String, subst: Subst = nullSubst()): User
  fun setSymbol(name: String, value: Symbol): Value

  fun setSymbol(name: String, type: Ty, variable: User): Value {
    return setSymbol(name, ValueSymbol(type, variable))
  }

  fun setSymbolLazy(name: String, type: Ty, lazyValue: CodegenContext.() -> Value): Value {
    return setSymbol(name, LazySymbol(type, name, lazyValue))
  }

  fun findFunction(name: String): Symbol?
  fun findModule(name: String): ScopeContext?
  fun findStruct(name: String): Type?
  fun findAlloca(name: String, subst: Subst = nullSubst()): User?
  fun findIntrinsic(name: String): IntrinsicFunction?

  fun lazyLocal(name: String, builder: () -> AllocaInst?): AllocaInst?

  fun Symbol.access(subst: Subst = nullSubst()): User? = with(this@CodegenContext) { access(subst) }

  fun Ty.typegen(): Type = typegen(this.ap(subst))
  fun Collection<Ty>.typegen(): List<Type> = map { it.typegen() }

  fun CodegenInstruction.codegen(): Value = with(this@CodegenContext) { codegen() }
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

class ExecContext(
  override val enclosing: ScopeContext,
  val function: Function,
  val returnType: Type,
  val arguments: MutableMap<String, Value> = linkedMapOf(),
  override val subst: Subst = enclosing.subst,
) : CodegenContext by enclosing

class DescriptorContext(
  val descriptor: ResolvedPlankElement,
  override val enclosing: ScopeContext,
  override val subst: Subst = enclosing.subst,
) : CodegenContext by enclosing {
  override val loc: Loc = descriptor.loc
}

data class ScopeContext(
  private val llvm: Context,
  override val file: ResolvedPlankFile,
  private val debugOptions: DebugOptions,
  private val intrinsics: MutableMap<String, IntrinsicFunction> = linkedMapOf(),
  override val scope: String = file.module.text,
  override val currentModule: Module = llvm.createModule(file.module.text),
  private val irBuilder: IRBuilder = llvm.createIRBuilder(),
  override val loc: Loc = file.loc,
  override val enclosing: CodegenContext? = null,
  override val subst: Subst = nullSubst(),
) : IRBuilder by irBuilder, Context by llvm, CodegenContext {
  /** TODO: add support for nested function intrinsics*/
  override val path: QualifiedPath = file.moduleName ?: QualifiedPath(file.module)

  override val unit: StructType by lazy {
    getOrCreateStruct("unit") { elements = listOf(i8) }
  }

  override val debug: DebugContext by lazy {
    DebugContext(this, debugOptions)
  }

  private val functions = mutableMapOf<String, Symbol>()
  private val symbols = mutableMapOf<String, Symbol>()
  private val structs = mutableMapOf<String, Type>()
  private val lazy = mutableMapOf<String, AllocaInst>()

  private val expanded = mutableListOf<ScopeContext>()
  private val modules = mutableMapOf<String, ScopeContext>()

  fun addIntrinsics(intrinsics: Intrinsics) {
    this.intrinsics += intrinsics.toFunctionMap(this)
  }

  override fun expand(scope: ScopeContext) {
    expanded += scope
  }

  override fun addModule(module: ScopeContext) {
    modules[module.scope] = module
  }

  override fun addFunction(function: FunctionSymbol, isGeneric: Boolean): Value {
    val symbol = if (isGeneric) RankedSymbol(function) else function
    return symbol.also { functions[function.name] = it }.codegen()
  }

  override fun addStruct(name: String, struct: Type) {
    structs[name] = struct
  }

  override fun getSymbol(scope: CodegenContext, name: String, subst: Subst): User {
    return findAlloca(name, subst)
      ?: findFunction(name)?.run { with(scope) { access(subst) } }
      ?: codegenError("Unresolved symbol `$name`")
  }

  override fun setSymbol(name: String, value: Symbol): Value {
    symbols[name] = value
    return value.codegen()
  }

  override fun findFunction(name: String): Symbol? {
    return functions[name]
      ?: enclosing?.findFunction(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findFunction(name) }
  }

  override fun findModule(name: String): ScopeContext? {
    return modules[name]
      ?: enclosing?.findModule(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findModule(name) }
  }

  override fun findStruct(name: String): Type? {
    return structs[name]
      ?: enclosing?.findStruct(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findStruct(name) }
  }

  override fun findAlloca(name: String, subst: Subst): User? {
    return symbols[name]?.access(subst)
      ?: enclosing?.findAlloca(name, subst)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findAlloca(name, subst) }
  }

  override fun findIntrinsic(name: String): IntrinsicFunction? {
    return intrinsics[name]
      ?: enclosing?.findIntrinsic(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findIntrinsic(name) }
  }

  override fun lazyLocal(name: String, builder: () -> AllocaInst?): AllocaInst? {
    return lazy[name] ?: run {
      val value = builder()

      if (value != null) {
        lazy[name] = value
      }

      value
    }
  }

  override fun toString(): String = "ScopeContext(scope=$scope, enclosing=$enclosing)"

  override fun close() {
    llvm.close()
    irBuilder.close()
  }
}

fun CodegenContext.ap(subst: Subst): CodegenContext {
  return when (this) {
    is DescriptorContext -> DescriptorContext(descriptor, enclosing, subst)
    is ExecContext -> ExecContext(enclosing, function, returnType, arguments, subst)
    is ScopeContext -> copy(subst = subst)
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
  builder: ScopeContext.() -> Unit = {},
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

fun CodegenContext.createFileContext(file: ResolvedPlankFile = this.file): ScopeContext =
  scopeContext().copy(enclosing = this, file = file, scope = file.module.text)
