package com.lorenzoog.plank.compiler

import com.lorenzoog.plank.analyzer.BindingContext
import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.compiler.converter.DataTypeConverter
import com.lorenzoog.plank.compiler.converter.DefaultDataTypeConverter
import com.lorenzoog.plank.compiler.instructions.CodegenError
import com.lorenzoog.plank.compiler.instructions.CodegenResult
import com.lorenzoog.plank.compiler.instructions.PlankInstruction
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
import org.llvm4j.llvm4j.Context as LLVMContext
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.IRBuilder
import org.llvm4j.llvm4j.Module as LLVMModule
import org.llvm4j.llvm4j.NamedStructType
import org.llvm4j.llvm4j.Value

data class PlankContext(
  val binding: BindingContext,
  val context: LLVMContext,
  val module: LLVMModule,
  val builder: IRBuilder,
  val runtime: PlankRuntime,
  val currentFile: PlankFile,
  val dataTypeConverter: DataTypeConverter,
  val moduleName: String,
  val mangler: NameMangler = NameMangler(),
  private val mapper: InstructionMapper = InstructionMapper(binding),
  private val enclosing: PlankContext? = null,
) {
  private val functions = mutableMapOf<String, IRFunction>()
  private val values = mutableMapOf<String, AllocaInstruction>()
  private val types = mutableMapOf<String, NamedStructType>()

  private val expanded = mutableListOf<PlankContext>()
  private val modules = mutableMapOf<String, PlankContext>()

  fun Value.toFloat(): Value {
    return dataTypeConverter.convertToFloat(this@PlankContext, this)
  }

  fun Value.toInt(): Value {
    return dataTypeConverter.convertToInt(this@PlankContext, this)
  }

  fun PlankType.toType(): TypegenResult {
    return toType(this)
  }

  fun PlankInstruction.codegen(): CodegenResult {
    return this@PlankContext.run {
      codegen()
    }
  }

  fun PlankElement.toInstruction(): PlankInstruction {
    return when (this) {
      is Expr -> mapper.visit(this)
      is Stmt -> mapper.visit(this)
      else -> TODO()
    }
  }

  fun createFileScope(file: PlankFile = currentFile): PlankContext = copy(
    enclosing = this,
    currentFile = file,
    moduleName = file.module,
  )

  fun createNestedScope(moduleName: String): PlankContext = copy(
    enclosing = this,
    moduleName = moduleName,
  )

  fun addFunction(decl: Decl.FunDecl): Either<out CodegenError, out Function> {
    val name = decl.name.text
    val mangledName = mangler.mangle(this, decl)
    val irFunction = IRNamedFunction(name, mangledName, decl)

    functions[name] = irFunction

    return irFunction.run {
      this@PlankContext.codegen()
    }
  }

  fun addStruct(name: String, struct: NamedStructType) {
    types[name] = struct
  }

  fun addVariable(name: String, variable: AllocaInstruction) {
    values[name] = variable
  }

  fun expand(module: PlankContext) {
    expanded += module
  }

  fun addModule(module: PlankContext) {
    modules[module.moduleName] = module
  }

  fun findModule(name: String): PlankContext? {
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
    fun of(file: PlankFile, bindingContext: BindingContext, module: LLVMModule): PlankContext {
      val builder = module.getContext().newIRBuilder()

      return PlankContext(
        binding = bindingContext,
        context = module.getContext(),
        module = module,
        builder = builder,
        runtime = PlankRuntime(module),
        currentFile = file,
        dataTypeConverter = DefaultDataTypeConverter(),
        moduleName = file.module,
      )
    }
  }
}
