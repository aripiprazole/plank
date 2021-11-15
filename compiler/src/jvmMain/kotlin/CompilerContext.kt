package com.gabrielleeg1.plank.compiler

import arrow.core.Either
import com.gabrielleeg1.plank.analyzer.BindingContext
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.compiler.converter.DataTypeConverter
import com.gabrielleeg1.plank.compiler.instructions.CodegenError
import com.gabrielleeg1.plank.compiler.instructions.CodegenResult
import com.gabrielleeg1.plank.compiler.instructions.CompilerInstruction
import com.gabrielleeg1.plank.compiler.instructions.element.IRFunction
import com.gabrielleeg1.plank.compiler.instructions.element.IRNamedFunction
import com.gabrielleeg1.plank.compiler.mangler.NameMangler
import com.gabrielleeg1.plank.compiler.runtime.PlankRuntime
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.grammar.element.PlankElement
import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.grammar.element.Stmt
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.Context
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.IRBuilder
import org.llvm4j.llvm4j.Module
import org.llvm4j.llvm4j.NamedStructType
import org.llvm4j.llvm4j.Value

data class CompilerContext(
  val debug: Boolean,
  val binding: BindingContext,
  val context: Context,
  val module: Module,
  val builder: IRBuilder,
  val runtime: PlankRuntime,
  val currentFile: PlankFile,
  val dataTypeConverter: DataTypeConverter,
  val moduleName: String,
  val mangler: NameMangler = NameMangler(),
  private val mapper: InstructionMapper = InstructionMapper(binding),
  private val enclosing: CompilerContext? = null,
) {
  private val functions = mutableMapOf<String, IRFunction>()
  private val values = mutableMapOf<String, Pair<PlankType, AllocaInstruction>>()
  private val types = mutableMapOf<String, Pair<PlankType, NamedStructType>>()

  private val expanded = mutableListOf<CompilerContext>()
  private val modules = mutableMapOf<String, CompilerContext>()

  inline fun debug(action: DebugCompilerContext.() -> Unit) {
    if (debug) {
      action(DebugCompilerContext(this))
    }
  }

  fun Value.toFloat(): CodegenResult {
    return dataTypeConverter.convertToFloat(this@CompilerContext, this)
  }

  fun Value.toInt(): CodegenResult {
    return dataTypeConverter.convertToInt(this@CompilerContext, this)
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
      is Expr -> mapper.visit(this)
      is Stmt -> mapper.visit(this)
      else -> TODO()
    }
  }

  fun createFileScope(file: PlankFile = currentFile): CompilerContext = copy(
    enclosing = this,
    currentFile = file,
    moduleName = file.module.text,
  )

  inline fun createNestedScope(
    moduleName: String,
    builder: CompilerContext.() -> Unit
  ): CompilerContext = copy(enclosing = this, moduleName = moduleName).apply(builder)

  fun addFunction(function: IRFunction): Either<CodegenError, Function> {
    functions[function.name] = function

    return with(function) {
      this@CompilerContext.codegen()
    }
  }

  fun addFunction(decl: ResolvedFunDecl): Either<CodegenError, Function> {
    val name = decl.name.text
    val mangledName = mangler.mangle(this, decl)
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
    modules[module.moduleName] = module
  }

  fun findModule(name: String): CompilerContext? {
    return modules[name]
      ?: enclosing?.findModule(name)
      ?: expanded.filter { it != this }.mapNotNull { it.findModule(name) }.firstOrNull()
  }

  fun findFunction(name: String): IRFunction? {
    return functions[name]
      ?: enclosing?.findFunction(name)
      ?: expanded.filter { it != this }.mapNotNull { it.findFunction(name) }.firstOrNull()
  }

  fun findType(predicate: (Pair<PlankType, NamedStructType>) -> Boolean): PlankType? {
    return types.values.find(predicate)?.first
      ?: enclosing?.findType(predicate)
      ?: expanded.filter { it != this }.mapNotNull { it.findType(predicate) }.firstOrNull()
  }

  fun findStruct(name: String): NamedStructType? {
    return types[name]?.second
      ?: enclosing?.findStruct(name)
      ?: expanded.filter { it != this }.mapNotNull { it.findStruct(name) }.firstOrNull()
  }

  fun findVariable(name: String): AllocaInstruction? {
    return values[name]?.second
      ?: enclosing?.findVariable(name)
      ?: expanded.filter { it != this }.mapNotNull { it.findVariable(name) }.firstOrNull()
  }

  override fun toString(): String {
    return "PlankContext($moduleName, $enclosing)"
  }

  companion object {
    fun of(
      file: PlankFile,
      binding: BindingContext,
      module: Module,
      debug: Boolean
    ): CompilerContext {
      val builder = module.getContext().newIRBuilder()

      return CompilerContext(
        debug = debug,
        binding = binding,
        context = module.getContext(),
        module = module,
        builder = builder,
        runtime = PlankRuntime(module),
        currentFile = file,
        dataTypeConverter = DataTypeConverter(),
        moduleName = file.module.text,
      )
    }
  }
}
