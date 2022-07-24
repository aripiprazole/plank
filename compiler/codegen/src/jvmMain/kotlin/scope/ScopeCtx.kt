package org.plank.codegen.scope

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import org.plank.analyzer.element.ResolvedPlankFile
import org.plank.analyzer.infer.Subst
import org.plank.analyzer.infer.nullSubst
import org.plank.codegen.DebugContext
import org.plank.codegen.DebugOptions
import org.plank.codegen.codegenError
import org.plank.codegen.element.FunctionSymbol
import org.plank.codegen.element.RankedSymbol
import org.plank.codegen.element.Symbol
import org.plank.codegen.getOrCreateStruct
import org.plank.codegen.intrinsics.IntrinsicFunction
import org.plank.codegen.intrinsics.Intrinsics
import org.plank.codegen.type.CodegenType
import org.plank.codegen.type.RankedType
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

data class ScopeCtx(
  private val llvm: Context,
  override val file: ResolvedPlankFile,
  private val debugOptions: DebugOptions,
  private val intrinsics: MutableMap<String, IntrinsicFunction> = linkedMapOf(),
  override val scope: String = file.module.text,
  override val currentModule: Module = llvm.createModule(file.module.text),
  private val irBuilder: IRBuilder = llvm.createIRBuilder(),
  override val loc: Loc = file.loc,
  override val enclosing: CodegenCtx? = null,
  override var subst: Subst = nullSubst(),

  private val functions: MutableMap<String, Symbol> = mutableMapOf(),
  private val symbols: MutableMap<String, Symbol> = mutableMapOf(),
  private val types: MutableMap<String, CodegenType> = mutableMapOf(),
  private val structs: MutableMap<String, Type> = mutableMapOf(),
  private val lazy: MutableMap<String, AllocaInst> = mutableMapOf(),
) : IRBuilder by irBuilder, Context by llvm, CodegenCtx {
  /** TODO: add support for nested function intrinsics*/
  override val path: QualifiedPath = file.moduleName ?: QualifiedPath(file.module)

  override val unit: StructType by lazy {
    getOrCreateStruct("unit") { elements = listOf(i8) }
  }

  override val debug: DebugContext by lazy {
    DebugContext(this, debugOptions)
  }

  private val expanded = mutableListOf<ScopeCtx>()
  private val modules = mutableMapOf<String, ScopeCtx>()

  fun addIntrinsics(intrinsics: Intrinsics) {
    this.intrinsics += intrinsics.toFunctionMap(this)
  }

  override fun expand(scope: ScopeCtx) {
    expanded += scope
  }

  override fun addModule(module: ScopeCtx) {
    modules[module.scope] = module
  }

  override fun addFunction(function: FunctionSymbol): Value {
    val symbol = RankedSymbol(function, function.scheme)
    return symbol.also { functions[function.name] = it }.codegen()
  }

  override fun addType(name: String, type: RankedType): CodegenType {
    return type.apply {
      declare()
      types[name] = type
      genSubTypes(type)
      codegen()
    }
  }

  override fun addStruct(name: String, type: Type) {
    structs[name] = type
  }

  override fun getSymbol(scope: CodegenCtx, name: String, subst: Subst): User {
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

  override fun findModule(name: String): ScopeCtx? {
    return modules[name]
      ?: enclosing?.findModule(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findModule(name) }
  }

  override fun findType(name: String): CodegenType? {
    return types[name]
      ?: enclosing?.findType(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findType(name) }
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

  override fun toString(): String = "ScopeCtx(:$scope) <: $enclosing"

  override fun close() {
    llvm.close()
    irBuilder.close()
  }
}

fun CodegenCtx.ap(subst: Subst): CodegenCtx {
  return when (this) {
    is DescriptorCtx -> DescriptorCtx(descriptor, enclosing, subst)
    is ExecCtx -> ExecCtx(enclosing, function, returnType, arguments, subst)
    else -> (this as ScopeCtx).copy(subst = subst)
  }
}

fun CodegenCtx.scopeContext(): ScopeCtx {
  return when (this) {
    is DescriptorCtx -> enclosing
    is ExecCtx -> enclosing
    else -> this as ScopeCtx
  }
}

@OptIn(ExperimentalContracts::class)
inline fun CodegenCtx.createScopeContext(
  moduleName: String,
  builder: ScopeCtx.() -> Unit = {},
): ScopeCtx {
  contract {
    callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
  }

  return when (this) {
    is DescriptorCtx -> enclosing.copy(
      enclosing = enclosing,
      scope = moduleName,
      functions = mutableMapOf(),
      symbols = mutableMapOf(),
      types = mutableMapOf(),
      structs = mutableMapOf(),
      lazy = mutableMapOf(),
    ).apply(builder)

    is ExecCtx -> enclosing.copy(
      enclosing = enclosing,
      scope = moduleName,
      functions = mutableMapOf(),
      symbols = mutableMapOf(),
      types = mutableMapOf(),
      structs = mutableMapOf(),
      lazy = mutableMapOf(),
    ).apply(builder)

    else -> (this as ScopeCtx).copy(
      enclosing = this,
      scope = moduleName,
      functions = mutableMapOf(),
      symbols = mutableMapOf(),
      types = mutableMapOf(),
      structs = mutableMapOf(),
      lazy = mutableMapOf(),
    ).apply(builder)
  }
}

fun CodegenCtx.createFileContext(file: ResolvedPlankFile = this.file): ScopeCtx {
  return scopeContext().copy(
    enclosing = this,
    file = file,
    scope = file.module.text,
    functions = mutableMapOf(),
    symbols = mutableMapOf(),
    types = mutableMapOf(),
    structs = mutableMapOf(),
    lazy = mutableMapOf(),
  )
}
