package com.gabrielleeg1.plank.compiler

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import com.gabrielleeg1.plank.analyzer.CharType
import com.gabrielleeg1.plank.analyzer.IntType
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.PointerType
import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.analyzer.element.ResolvedPlankFile
import com.gabrielleeg1.plank.analyzer.element.ResolvedStmt
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.compiler.builder.alloca
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.IRFunction
import com.gabrielleeg1.plank.compiler.instructions.element.IRNamedFunction
import com.gabrielleeg1.plank.compiler.instructions.unresolvedVariableError
import com.gabrielleeg1.plank.grammar.element.PlankElement
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.Context
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.IRBuilder
import org.llvm4j.llvm4j.Module
import org.llvm4j.llvm4j.NamedStructType

data class CompilerContext(
  val debug: Boolean,
  val module: Module,
  val currentFile: ResolvedPlankFile,
  val contextName: String = currentFile.module.text,
  val context: Context = module.getContext(),
  val builder: IRBuilder = module.getContext().newIRBuilder(),
  val runtime: PlankRuntime = PlankRuntime(module),
  private val mapper: InstructionMapper = InstructionMapper,
  private val enclosing: CompilerContext? = null,
) {
  private val _references = LinkedHashMap<String, PlankType>().apply {
    put("x", PointerType(CharType)) // TODO: remove me
  }

  private val functions = mutableMapOf<String, IRFunction>()
  private val values = mutableMapOf<String, Pair<PlankType, AllocaInstruction>>()
  private val types = mutableMapOf<String, Pair<PlankType, NamedStructType>>()

  private val expanded = mutableListOf<CompilerContext>()
  private val modules = mutableMapOf<String, CompilerContext>()

  val references: Map<String, PlankType>
    get() = _references

  inline fun debug(action: DebugCompilerContext.() -> Unit) {
    if (debug) {
      action(DebugCompilerContext(this))
    }
  }

  fun PlankType.toType(): TypegenResult {
    return toType(this)
  }

  fun CompilerInstruction.codegen(): CodegenResult {
    return this@CompilerContext.run {
      codegen()
    }
  }

  fun PlankElement.toInstruction(): CompilerInstruction {
    return when (this) {
      is TypedExpr -> mapper.visit(this)
      is ResolvedStmt -> mapper.visit(this)
      else -> TODO("Not implemented mapping for ${this::class.simpleName}")
    }
  }

  fun createFileScope(file: ResolvedPlankFile = currentFile): CompilerContext = copy(
    enclosing = this,
    currentFile = file,
    contextName = file.module.text,
  )

  inline fun createNestedScope(
    moduleName: String,
    builder: CompilerContext.() -> Unit = {},
  ): CompilerContext = copy(enclosing = this, contextName = moduleName).apply(builder)

  inline operator fun invoke(builder: CompilerContext.() -> Unit): CompilerContext {
    return apply(builder)
  }

  fun addFunction(
    name: String,
    mangledName: String,
    descriptor: ResolvedFunDecl,
  ): Either<CodegenViolation, Unit> {
    functions[name] = IRNamedFunction(name, mangledName, descriptor)

    return Unit.right()
  }

  fun addFunction(function: IRFunction): Either<CodegenViolation, Function> {
    functions[function.name] = function

    return with(function) {
      this@CompilerContext.codegen()
    }
  }

  fun addFunction(decl: ResolvedFunDecl): Either<CodegenViolation, Function> {
    val name = decl.name.text
    val mangledName = mangleFunction(decl)
    val function = IRNamedFunction(name, mangledName, decl)

    functions[name] = function

    return with(function) {
      this@CompilerContext.codegen()
    }
  }

  fun addStruct(name: String, type: PlankType, struct: NamedStructType) {
    types[name] = type to struct
  }

  fun addVariable(name: String, type: PlankType, variable: AllocaInstruction) {
    values[name] = type to variable
  }

  fun expand(module: CompilerContext) {
    expanded += module
  }

  fun addModule(module: CompilerContext) {
    modules[module.contextName] = module
  }

  fun findModule(name: String): CompilerContext? {
    return modules[name]
      ?: enclosing?.findModule(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findModule(name) }
  }

  fun findFunction(name: String): IRFunction? {
    return functions[name]
      ?: enclosing?.findFunction(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findFunction(name) }
  }

  fun findStruct(name: String): NamedStructType? {
    return types[name]?.second
      ?: enclosing?.findStruct(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findStruct(name) }
  }

  fun findVariable(name: String): Either<CodegenViolation, AllocaInstruction> = either.eager {
    findVariableAlloca(name)
      ?: findFunction(name)
        ?.accessIn(this@CompilerContext)
        ?.let(::alloca)
      ?: unresolvedVariableError(name).left().bind<AllocaInstruction>()
  }

  fun findVariableAlloca(name: String): AllocaInstruction? {
    return values[name]?.second
      ?: enclosing?.findVariableAlloca(name)
      ?: expanded.filter { it != this }.firstNotNullOfOrNull { it.findVariableAlloca(name) }
  }

  override fun toString(): String {
    return "PlankContext($contextName, $enclosing)"
  }
}
