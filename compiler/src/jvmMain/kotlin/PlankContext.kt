package com.lorenzoog.jplank.compiler

import com.lorenzoog.jplank.analyzer.BindingContext
import com.lorenzoog.jplank.analyzer.type.PkType
import com.lorenzoog.jplank.compiler.instructions.PlankInstruction
import com.lorenzoog.jplank.compiler.runtime.PlankRuntime
import com.lorenzoog.jplank.compiler.utils.FunctionUtils
import com.lorenzoog.jplank.element.Decl
import com.lorenzoog.jplank.element.Expr
import com.lorenzoog.jplank.element.PkElement
import com.lorenzoog.jplank.element.PkFile
import com.lorenzoog.jplank.element.Stmt
import io.vexelabs.bitbuilder.llvm.ir.Builder
import io.vexelabs.bitbuilder.llvm.ir.Type
import io.vexelabs.bitbuilder.llvm.ir.instructions.AllocaInstruction
import io.vexelabs.bitbuilder.llvm.ir.types.StructType
import io.vexelabs.bitbuilder.llvm.ir.values.FunctionValue
import io.vexelabs.bitbuilder.llvm.ir.Context as LLVMContext
import io.vexelabs.bitbuilder.llvm.ir.Module as LLVMModule

data class PlankContext(
  val binding: BindingContext,
  val llvm: LLVMContext,
  val module: LLVMModule,
  val builder: Builder,
  val runtime: PlankRuntime,
  val currentFile: PkFile,
  private val enclosing: PlankContext?,
  private val values: MutableMap<String, AllocaInstruction>,
  private val types: MutableMap<String, StructType>,
  private val mapper: InstructionMapper,
  private val _errors: MutableMap<PkElement?, String>
) {
  val main: FunctionValue?
    get() = module.getFunction(FunctionUtils.generateName("main", this))

  val errors: Map<PkElement?, String>
    get() = _errors

  fun map(expr: Expr): PlankInstruction = mapper.visit(expr)
  fun map(stmt: Stmt): PlankInstruction = mapper.visit(stmt)
  fun map(decl: Decl): PlankInstruction = mapper.visit(decl)
  fun map(type: PkType?): Type? = mapper.typeMapper.map(this, type)

  @JvmName("mapDecls")
  fun map(decls: List<Decl>): List<PlankInstruction> = decls.map { mapper.visit(it) }

  @JvmName("mapStmts")
  fun map(stmts: List<Stmt>): List<PlankInstruction> = stmts.map { mapper.visit(it) }

  fun createScope(file: PkFile = currentFile): PlankContext = copy(
    enclosing = this,
    currentFile = file,
    values = mutableMapOf(),
    types = mutableMapOf()
  )

  fun <T> report(error: String, descriptor: PkElement? = null): T? {
    _errors[descriptor] = error

    return null
  }

  fun addStructure(name: String, struct: StructType) {
    types[name] = struct
  }

  fun addVariable(name: String, variable: AllocaInstruction) {
    values[name] = variable
  }

  fun findStructure(name: String): StructType? {
    return types[name] ?: return enclosing?.findStructure(name)
  }

  fun findVariable(name: String): AllocaInstruction? {
    return values[name] ?: return enclosing?.findVariable(name)
  }

  companion object {
    fun of(
      currentFile: PkFile,
      instructionMapper: InstructionMapper,
      bindingContext: BindingContext,
      module: LLVMModule
    ): PlankContext {
      return PlankContext(
        binding = bindingContext,
        llvm = module.getContext(),
        module = module,
        builder = module.getContext().createBuilder(),
        runtime = PlankRuntime(module),
        currentFile = currentFile,
        enclosing = null,
        values = mutableMapOf(),
        types = mutableMapOf(),
        mapper = instructionMapper,
        _errors = mutableMapOf()
      )
    }
  }
}
