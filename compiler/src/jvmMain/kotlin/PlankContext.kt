package com.lorenzoog.jplank.compiler

import com.lorenzoog.jplank.analyzer.BindingContext
import com.lorenzoog.jplank.analyzer.type.PlankType
import com.lorenzoog.jplank.compiler.converter.DataTypeConverter
import com.lorenzoog.jplank.compiler.converter.DefaultDataTypeConverter
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.compiler.instructions.element.IRFunction
import com.lorenzoog.jplank.compiler.instructions.element.IRNamedFunction
import com.lorenzoog.jplank.compiler.llvm.newBuilder
import com.lorenzoog.jplank.compiler.mangler.Mangler
import com.lorenzoog.jplank.compiler.mangler.SimpleMangler
import com.lorenzoog.jplank.compiler.runtime.PlankRuntime
import com.lorenzoog.jplank.element.Decl
import com.lorenzoog.jplank.element.Expr
import com.lorenzoog.jplank.element.PlankElement
import com.lorenzoog.jplank.element.PlankFile
import com.lorenzoog.jplank.element.Stmt
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.Builder
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.NamedStructType
import org.llvm4j.llvm4j.Type
import org.llvm4j.llvm4j.Context as LLVMContext
import org.llvm4j.llvm4j.Module as LLVMModule

data class PlankContext(
  val binding: BindingContext,
  val llvm: LLVMContext,
  val module: LLVMModule,
  val builder: Builder,
  val runtime: PlankRuntime,
  val currentFile: PlankFile,
  val mangler: Mangler,
  val dataTypeConverter: DataTypeConverter,
  private val enclosing: PlankContext?,
  private val values: MutableMap<String, AllocaInstruction>,
  private val types: MutableMap<String, NamedStructType>,
  private val functions: MutableMap<String, IRFunction>,
  private val mapper: InstructionMapper,
  private val _errors: MutableMap<PlankElement?, String>
) {
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

  fun createScope(file: PlankFile = currentFile): PlankContext = copy(
    enclosing = this,
    currentFile = file,
    values = mutableMapOf(),
    types = mutableMapOf(),
  )

  fun <T> report(error: String, descriptor: PlankElement? = null): T? {
    _errors[descriptor] = error

    return null
  }

  fun addFunction(decl: Decl.FunDecl): Function? {
    val name = decl.name.text ?: return report("name is null", decl)
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

  fun findFunction(name: String): IRFunction? {
    return functions[name] ?: return enclosing?.findFunction(name)
  }

  fun findStructure(name: String): NamedStructType? {
    return types[name] ?: return enclosing?.findStructure(name)
  }

  fun findVariable(name: String): AllocaInstruction? {
    return values[name] ?: return enclosing?.findVariable(name)
  }

  override fun toString(): String {
    return "PlankContext(${currentFile.module}, $enclosing)"
  }

  companion object {
    fun of(
        currentFile: PlankFile,
        instructionMapper: InstructionMapper,
        bindingContext: BindingContext,
        module: LLVMModule
    ): PlankContext {
      val builder = module.getContext().newBuilder()

      return PlankContext(
        binding = bindingContext,
        llvm = module.getContext(),
        module = module,
        builder = builder,
        runtime = PlankRuntime(builder, module),
        currentFile = currentFile,
        mangler = SimpleMangler(),
        dataTypeConverter = DefaultDataTypeConverter(),
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
