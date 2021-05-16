package com.lorenzoog.plank.compiler

import com.lorenzoog.plank.analyzer.BindingContext
import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.compiler.converter.DataTypeConverter
import com.lorenzoog.plank.compiler.instructions.CodegenError
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.CompilerInstruction
import com.lorenzoog.plank.compiler.instructions.element.IRFunction
import com.lorenzoog.plank.compiler.instructions.element.IRNamedFunction
import com.lorenzoog.plank.compiler.mangler.NameMangler
import com.lorenzoog.plank.compiler.runtime.PlankRuntime
import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.PlankElement
import com.lorenzoog.plank.grammar.element.PlankFile
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.shared.Either
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.Context
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.IRBuilder
import org.llvm4j.llvm4j.Module
import org.llvm4j.llvm4j.NamedStructType
import org.llvm4j.llvm4j.Value

data class CompilerContext(
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
  private val values = mutableMapOf<String, AllocaInstruction>()
  private val types = mutableMapOf<String, NamedStructType>()

  private val expanded = mutableListOf<CompilerContext>()
  private val modules = mutableMapOf<String, CompilerContext>()

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
    moduleName = file.module,
  )

  inline fun createNestedScope(
    moduleName: String,
    builder: CompilerContext.() -> Unit
  ): CompilerContext = copy(enclosing = this, moduleName = moduleName).apply(builder)

  fun addFunction(decl: Decl.FunDecl): Either<CodegenError, Function> {
    val name = decl.name.text
    val mangledName = mangler.mangle(this, decl)
    val irFunction = IRNamedFunction(name, mangledName, decl)

    functions[name] = irFunction

    return irFunction.run {
      this@CompilerContext.codegen()
    }
  }

  fun addStruct(name: String, struct: NamedStructType) {
    types[name] = struct
  }

  fun addVariable(name: String, variable: AllocaInstruction) {
    values[name] = variable
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

  fun findStruct(name: String): NamedStructType? {
    return types[name]
      ?: enclosing?.findStruct(name)
      ?: expanded.filter { it != this }.mapNotNull { it.findStruct(name) }.firstOrNull()
  }

  fun findVariable(name: String): AllocaInstruction? {
    return values[name]
      ?: enclosing?.findVariable(name)
      ?: expanded.filter { it != this }.mapNotNull { it.findVariable(name) }.firstOrNull()
  }

  override fun toString(): String {
    return "PlankContext($moduleName, $enclosing)"
  }

  companion object {
    fun of(file: PlankFile, bindingContext: BindingContext, module: Module): CompilerContext {
      val builder = module.getContext().newIRBuilder()

      return CompilerContext(
        binding = bindingContext,
        context = module.getContext(),
        module = module,
        builder = builder,
        runtime = PlankRuntime(module),
        currentFile = file,
        dataTypeConverter = DataTypeConverter(),
        moduleName = file.module,
      )
    }
  }
}