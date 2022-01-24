package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.element.ResolvedPlankElement
import com.gabrielleeg1.plank.analyzer.element.ResolvedPlankFile
import com.gabrielleeg1.plank.analyzer.element.ResolvedStmt
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.IRFunction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedVariableError
import com.gabrielleeg1.plank.grammar.element.Location
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.Context
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.IRBuilder
import org.llvm4j.llvm4j.Module
import org.llvm4j.llvm4j.NamedStructType
import org.llvm4j.llvm4j.Type
import org.llvm4j.llvm4j.Value

sealed interface CompilerContext {
  val name: String
  val debug: Boolean
  val module: Module
  val file: ResolvedPlankFile
  val context: Context
  val builder: IRBuilder
  val runtime: Runtime
  val mapper: InstructionMapper
  val enclosing: CompilerContext?
  val location: Location

  fun addFunction(function: IRFunction): Function
  fun addStruct(name: String, type: PlankType, struct: NamedStructType)
  fun addVariable(name: String, type: PlankType, variable: AllocaInstruction)
  fun expand(module: ScopeContext)
  fun addModule(module: ScopeContext)
  fun findModule(name: String): ScopeContext?
  fun findFunction(name: String): IRFunction?
  fun findStruct(name: String): NamedStructType?
  fun findVariable(name: String): AllocaInstruction
  fun findAlloca(name: String): AllocaInstruction?

  fun PlankType.typegen(): Type = typegen(this)

  fun CompilerInstruction.codegen(): Value = this@CompilerContext.run { codegen() }

  fun Collection<ResolvedPlankElement>.codegen(): List<Value> {
    return map { it.codegen() }
  }

  fun ResolvedPlankElement.codegen(name: String = "CodegenContext"): Value =
    CodegenContext(this, scopeContext()).let { context ->
      when (context.descriptor) {
        is TypedExpr -> mapper.visit(context.descriptor).run { context.codegen() }
        is ResolvedStmt -> mapper.visit(context.descriptor).run { context.codegen() }
        else -> error("No available value mapping for ${this::class.simpleName}")
      }
    }
}

data class CodegenContext(
  val descriptor: ResolvedPlankElement,
  override val enclosing: ScopeContext,
) : CompilerContext by enclosing {
  override val location: Location = descriptor.location

  inline operator fun invoke(builder: CodegenContext.() -> Unit): CodegenContext = apply(builder)
}

data class ExecutionContext(
  override val enclosing: ScopeContext,
  val parameters: MutableMap<String, Value> = LinkedHashMap(),
) : CompilerContext by enclosing

data class ScopeContext(
  override val debug: Boolean,
  override val module: Module,
  override val file: ResolvedPlankFile,
  override val name: String = file.module.text,
  override val context: Context = module.getContext(),
  override val builder: IRBuilder = module.getContext().newIRBuilder(),
  override val runtime: Runtime = Runtime(module),
  override val mapper: InstructionMapper = InstructionMapper,
  override val enclosing: ScopeContext? = null,
) : CompilerContext {
  override val location: Location = file.location

  private val functions = mutableMapOf<String, IRFunction>()
  private val values = mutableMapOf<String, Pair<PlankType, AllocaInstruction>>()
  private val types = mutableMapOf<String, Pair<PlankType, NamedStructType>>()

  private val expanded = mutableListOf<ScopeContext>()
  private val modules = mutableMapOf<String, ScopeContext>()

  inline operator fun invoke(builder: ScopeContext.() -> Unit): ScopeContext = apply(builder)

  fun createFileScope(file: ResolvedPlankFile = this.file): ScopeContext = copy(
    enclosing = this,
    file = file,
    name = file.module.text,
  )

  @Suppress("UNCHECKED_CAST")
  override fun addFunction(function: IRFunction): Function {
    functions[function.name] = function

    return function.codegen() as Function
  }

  override fun addStruct(name: String, type: PlankType, struct: NamedStructType) {
    types[name] = type to struct
  }

  override fun addVariable(name: String, type: PlankType, variable: AllocaInstruction) {
    values[name] = type to variable
  }

  override fun expand(module: ScopeContext) {
    expanded += module
  }

  override fun addModule(module: ScopeContext) {
    modules[module.name] = module
  }

  override fun findModule(name: String): ScopeContext? {
    return modules[name]
      ?: enclosing?.findModule(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findModule(name) }
  }

  override fun findFunction(name: String): IRFunction? {
    return functions[name]
      ?: enclosing?.findFunction(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findFunction(name) }
  }

  override fun findStruct(name: String): NamedStructType? {
    return types[name]?.second
      ?: enclosing?.findStruct(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findStruct(name) }
  }

  override fun findVariable(name: String): AllocaInstruction =
    findAlloca(name)
      ?: findFunction(name)?.accessIn(this@ScopeContext)
      ?: unresolvedVariableError(name)

  override fun findAlloca(name: String): AllocaInstruction? {
    return values[name]?.second
      ?: enclosing?.findAlloca(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findAlloca(name) }
  }

  override fun toString(): String {
    return "ScopeContext($name, $enclosing)"
  }
}

fun CompilerContext.scopeContext(): ScopeContext {
  return when (this) {
    is CodegenContext -> enclosing
    is ExecutionContext -> enclosing
    is ScopeContext -> this
  }
}

inline fun CompilerContext.createScopeContext(
  moduleName: String,
  builder: ScopeContext.() -> Unit = {}
): ScopeContext = when (this) {
  is ScopeContext -> copy(enclosing = this, name = moduleName).apply(builder)
  is CodegenContext -> enclosing.copy(enclosing = enclosing, name = moduleName).apply(builder)
  is ExecutionContext -> enclosing.copy(enclosing = enclosing, name = moduleName).apply(builder)
}

inline fun CompilerContext.debug(action: DebugCompilerContext.() -> Unit) {
  if (debug) {
    action(DebugCompilerContext(this))
  }
}
