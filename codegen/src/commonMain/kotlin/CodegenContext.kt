package org.plank.codegen

import org.plank.analyzer.PlankType
import org.plank.analyzer.element.ResolvedPlankElement
import org.plank.analyzer.element.ResolvedPlankFile
import org.plank.analyzer.element.ResolvedStmt
import org.plank.analyzer.element.TypedExpr
import org.plank.codegen.element.FunctionSymbol
import org.plank.codegen.element.LazySymbol
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
import org.plank.syntax.element.Location
import org.plank.syntax.element.QualifiedPath
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed interface CodegenContext : Context, IRBuilder {
  val scope: String
  val file: ResolvedPlankFile
  val debug: DebugContext
  val currentModule: Module
  val mapper: InstructionMapper
  val location: Location
  val path: QualifiedPath
  val enclosing: CodegenContext?

  val unit: StructType

  fun expand(scope: ScopeContext)
  fun addModule(module: ScopeContext)

  fun addFunction(function: FunctionSymbol): Value
  fun addStruct(name: String, type: PlankType, struct: StructType)

  fun getSymbol(name: String): User
  fun setSymbol(name: String, value: Symbol): Value

  fun setSymbol(name: String, type: PlankType, variable: User): Value {
    return setSymbol(name, ValueSymbol(type, variable))
  }

  fun setSymbolLazy(name: String, type: PlankType, lazyValue: CodegenContext.() -> Value): Value {
    return setSymbol(name, LazySymbol(type, name, lazyValue))
  }

  fun findFunction(name: String): FunctionSymbol?
  fun findModule(name: String): ScopeContext?
  fun findStruct(name: String): StructType?
  fun findAlloca(name: String): User?
  fun findIntrinsic(name: String): IntrinsicFunction?

  fun lazyLocal(name: String, builder: () -> AllocaInst?): AllocaInst?

  fun Symbol.access(): User? = with(this@CodegenContext) { access() }

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
  val function: Function,
  val returnType: Type,
  val arguments: MutableMap<String, Value> = linkedMapOf(),
) : CodegenContext by enclosing

class DescriptorContext(
  val descriptor: ResolvedPlankElement,
  override val enclosing: ScopeContext,
) : CodegenContext by enclosing {
  override val location: Location = descriptor.location
}

data class ScopeContext(
  private val llvm: Context,
  override val file: ResolvedPlankFile,
  private val debugOptions: DebugOptions,
  private val intrinsics: MutableMap<String, IntrinsicFunction> = linkedMapOf(),
  override val scope: String = file.module.text,
  override val currentModule: Module = llvm.createModule(file.module.text),
  private val irBuilder: IRBuilder = llvm.createIRBuilder(),
  override val mapper: InstructionMapper = InstructionMapper,
  override val location: Location = file.location,
  override val enclosing: CodegenContext? = null,
) : IRBuilder by irBuilder, Context by llvm, CodegenContext {
  /** TODO: add support for nested function intrinsics*/
  override val path: QualifiedPath = file.moduleName ?: QualifiedPath(file.module)

  override val unit: StructType by lazy {
    getOrCreateStruct("unit") { elements = listOf(i8) }
  }

  override val debug: DebugContext by lazy {
    DebugContext(this, debugOptions)
  }

  private val functions = mutableMapOf<String, FunctionSymbol>()
  private val symbols = mutableMapOf<String, Symbol>()
  private val structs = mutableMapOf<String, Pair<PlankType, StructType>>()
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

  override fun addFunction(function: FunctionSymbol): Value {
    functions[function.name] = function

    return function.codegen()
  }

  override fun addStruct(name: String, type: PlankType, struct: StructType) {
    structs[name] = type to struct
  }

  override fun getSymbol(name: String): User {
    return findAlloca(name)
      ?: findFunction(name)?.run { access() }
      ?: codegenError("Unresolved symbol `$name`")
  }

  override fun setSymbol(name: String, value: Symbol): Value {
    symbols[name] = value
    return value.codegen()
  }

  override fun findFunction(name: String): FunctionSymbol? {
    return functions[name]
      ?: enclosing?.findFunction(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findFunction(name) }
  }

  override fun findModule(name: String): ScopeContext? {
    return modules[name]
      ?: enclosing?.findModule(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findModule(name) }
  }

  override fun findStruct(name: String): StructType? {
    return structs[name]?.second
      ?: enclosing?.findStruct(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findStruct(name) }
  }

  override fun findAlloca(name: String): User? {
    return symbols[name]?.access()
      ?: enclosing?.findAlloca(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findAlloca(name) }
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

fun CodegenContext.createFileContext(file: ResolvedPlankFile = this.file): ScopeContext =
  scopeContext().copy(enclosing = this, file = file, scope = file.module.text)
