package com.lorenzoog.jplank.compiler

import com.lorenzoog.jplank.analyzer.BindingContext
import com.lorenzoog.jplank.analyzer.type.PlankType
import com.lorenzoog.jplank.compiler.converter.DataTypeConverter
import com.lorenzoog.jplank.compiler.converter.DefaultDataTypeConverter
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.compiler.instructions.element.IRFunction
import com.lorenzoog.jplank.compiler.instructions.element.IRNamedFunction
import com.lorenzoog.jplank.compiler.mangler.NameMangler
import com.lorenzoog.jplank.compiler.runtime.PlankRuntime
import com.lorenzoog.jplank.element.Decl
import com.lorenzoog.jplank.element.Expr
import com.lorenzoog.jplank.element.PlankElement
import com.lorenzoog.jplank.element.PlankFile
import com.lorenzoog.jplank.element.Stmt
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.Context as LLVMContext
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.IRBuilder
import org.llvm4j.llvm4j.Module as LLVMModule
import org.llvm4j.llvm4j.NamedStructType
import org.llvm4j.llvm4j.Type

data class PlankContext(
  val binding: BindingContext,
  val llvm: LLVMContext,
  val module: LLVMModule,
  val builder: IRBuilder,
  val runtime: PlankRuntime,
  val currentFile: PlankFile,
  val mangler: NameMangler,
  val dataTypeConverter: DataTypeConverter,
  val moduleName: String,
  private val enclosing: PlankContext?,
  private val values: MutableMap<String, AllocaInstruction>,
  private val types: MutableMap<String, NamedStructType>,
  private val functions: MutableMap<String, IRFunction>,
  private val mapper: InstructionMapper,
  private val _errors: MutableMap<PlankElement?, String>
) {
  private val expanded = mutableListOf<PlankContext>()
  private val modules = mutableMapOf<String, PlankContext>()

  val main: Function?
    get() {
      val main = currentFile.program
        .filterIsInstance<Decl.FunDecl>()
        .find { it.name.text == "main" }
        ?: return report("could not find entry point")

      return module.getFunction(mangler.mangle(this, main)).toNullable()
    }

  val errors: Map<PlankElement?, String>
    get() = _errors

  fun map(expr: Expr): PlankInstruction = mapper.visit(expr)
  fun map(stmt: Stmt): PlankInstruction = mapper.visit(stmt)
  fun map(decl: Decl): PlankInstruction = mapper.visit(decl)
  fun map(type: PlankType?): Type? = mapper.typeMapper.map(this, type)

  @JvmName("mapDecls")
  fun map(decls: List<Decl>): List<PlankInstruction> = decls.map { mapper.visit(it) }

  @JvmName("mapStmts")
  fun map(stmts: List<Stmt>): List<PlankInstruction> = stmts.map { mapper.visit(it) }

  fun createFileScope(file: PlankFile = currentFile): PlankContext = copy(
    enclosing = this,
    currentFile = file,
    moduleName = file.module,
    values = mutableMapOf(),
    types = mutableMapOf(),
    functions = mutableMapOf(),
  )

  fun createNestedScope(moduleName: String): PlankContext = copy(
    enclosing = this,
    moduleName = moduleName,
    values = mutableMapOf(),
    types = mutableMapOf(),
    functions = mutableMapOf(),
  )

  fun <T> report(error: String, descriptor: PlankElement? = null): T? {
    _errors[descriptor] = error

    return null
  }

  fun addFunction(decl: Decl.FunDecl): Function? {
    val name = decl.name.text
    val mangledName = mangler.mangle(this, decl)
    val irFunction = IRNamedFunction(name, mangledName, decl)

    functions[name] = irFunction

    return irFunction.codegen(this)
  }

  fun addStructure(name: String, struct: NamedStructType) {
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

  fun findStructure(name: String): NamedStructType? {
    return types[name]
      ?: enclosing?.findStructure(name)
      ?: expanded.filter { it != this }.mapNotNull { it.findStructure(name) }.firstOrNull()
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
    fun of(
      currentFile: PlankFile,
      instructionMapper: InstructionMapper,
      bindingContext: BindingContext,
      module: LLVMModule
    ): PlankContext {
      val builder = module.getContext().newIRBuilder()

      return PlankContext(
        binding = bindingContext,
        llvm = module.getContext(),
        module = module,
        builder = builder,
        runtime = PlankRuntime(module),
        currentFile = currentFile,
        mangler = NameMangler(),
        dataTypeConverter = DefaultDataTypeConverter(),
        moduleName = currentFile.module,
        enclosing = null,
        values = mutableMapOf(),
        functions = mutableMapOf(),
        types = mutableMapOf(),
        mapper = instructionMapper,
        _errors = mutableMapOf()
      )
    }
  }
}
