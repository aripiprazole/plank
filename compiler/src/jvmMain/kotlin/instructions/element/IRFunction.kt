package com.gabrielleeg1.plank.compiler.instructions.element

import arrow.core.computations.either
import com.gabrielleeg1.plank.analyzer.FunctionType
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.UnitType
import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.analyzer.element.ResolvedReturnStmt
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.builder.alloca
import com.gabrielleeg1.plank.compiler.builder.buildLoad
import com.gabrielleeg1.plank.compiler.builder.buildReturn
import com.gabrielleeg1.plank.compiler.builder.getField
import com.gabrielleeg1.plank.compiler.builder.getInstance
import com.gabrielleeg1.plank.compiler.builder.insertionBlock
import com.gabrielleeg1.plank.compiler.builder.pointerType
import com.gabrielleeg1.plank.compiler.createScopeContext
import com.gabrielleeg1.plank.compiler.instructions.CodegenViolation
import com.gabrielleeg1.plank.compiler.instructions.invalidFunctionError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedTypeError
import com.gabrielleeg1.plank.compiler.instructions.unresolvedVariableError
import com.gabrielleeg1.plank.compiler.mangleFunction
import com.gabrielleeg1.plank.compiler.unsafeCast
import com.gabrielleeg1.plank.compiler.verify
import com.gabrielleeg1.plank.grammar.element.Identifier
import org.llvm4j.llvm4j.AllocaInstruction
import org.llvm4j.llvm4j.Argument
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.PointerType

interface IRFunction : IRElement {
  val name: String
  val mangledName: String

  /** Access the function in the [context] */
  fun accessIn(context: CompilerContext): AllocaInstruction?

  /** Generates the function in the [this] */
  override fun CompilerContext.codegen(): Function
}

class IRNamedFunction(
  val type: FunctionType,
  override val name: String,
  override val mangledName: String,
  private val returnType: PlankType,
  private val realParameters: Map<Identifier, PlankType>,
  private val generateBody: (CompilerContext.(List<Argument>) -> Unit)? = null,
) : IRFunction {
  override fun accessIn(context: CompilerContext): AllocaInstruction? {
    return context.module
      .getFunction(mangledName)
      .map { context.alloca(it, "function_access_$name") }
      .toNullable()
  }

  override fun CompilerContext.codegen(): Function {
    val function = module.addFunction(
      mangledName,
      context.getFunctionType(
        returnType = returnType.typegen(),
        *realParameters.values
          .map { type ->
            type.cast<FunctionType>()?.copy(isClosure = true)?.typegen()
              ?.let { if (it is PointerType) it else context.getPointerType(it).unwrap() }
              ?: type.typegen()
          }
          .toTypedArray(),
        isVariadic = false,
      ),
    )

    if (generateBody == null) return function

    val enclosingBlock = runCatching { insertionBlock }

    createScopeContext(name) {
      builder.positionAfter(context.newBasicBlock("entry").also(function::addBasicBlock))

      function.getParameters()
        .mapIndexed(generateParameter(realParameters, this))

      generateBody.invoke(this, function.getParameters().toList())

      if (!function.verify()) {
        invalidFunctionError(function)
      }
    }

    if (enclosingBlock.isSuccess) {
      builder.positionAfter(enclosingBlock.getOrThrow())
    }

    return function
  }
}

class IRClosure(
  val type: FunctionType,
  override val name: String,
  override val mangledName: String,
  private val references: LinkedHashMap<Identifier, PlankType>,
  private val returnType: PlankType,
  private val realParameters: Map<Identifier, PlankType>,
  private val generateBody: CompilerContext.(List<Argument>) -> Unit,
  private val descriptor: ResolvedFunDecl? = null,
) : IRFunction {
  override fun accessIn(context: CompilerContext): AllocaInstruction {
    return context.findAlloca(mangledName)!!
  }

  override fun CompilerContext.codegen(): Function {
    val references = references.mapKeys { (name) -> name.text }

    val environmentType = context.getNamedStructType("Closure_${mangledName}_Environment").apply {
      setElementTypes(
        *references.map { it.value.typegen() }.toTypedArray(),
        isPacked = false
      )
    }

    val functionType = context.getFunctionType(
      returnType.typegen(),
      pointerType(environmentType),
      *realParameters.values.toList().map { type -> type.typegen() }.toTypedArray(),
    )

    val closureFunctionType = context.getNamedStructType("Closure_${mangledName}_Function").apply {
      setElementTypes(
        pointerType(functionType),
        pointerType(environmentType),
        isPacked = false
      )
    }

    val function = module.addFunction(mangledName, functionType)

    val enclosingBlock = insertionBlock // All closures are nested

    createScopeContext(name) {
      builder.positionAfter(context.newBasicBlock("entry").also(function::addBasicBlock))

      val environment = function.getParameter(0).unwrap().apply {
        setName("closure_environment")
      }

      references.entries.forEachIndexed { index, (reference, type) ->
        val variable = alloca(buildLoad(getField(environment, index)), "ENV.$reference")

        addVariable(reference, type, variable)
      }

      val parameters = function.getParameters().drop(1)

      parameters.forEachIndexed(generateParameter(realParameters, this))

      generateBody(parameters)

      if (!function.verify()) {
        invalidFunctionError(function)
      }
    }

    builder.positionAfter(enclosingBlock)

    val variables = references.keys
      .mapNotNull { findAlloca(it) }
      .map { buildLoad(it) }
      .toTypedArray()

    val environment = getInstance(environmentType, *variables, isPointer = true)
    val closure = getInstance(closureFunctionType, function, environment, isPointer = true)

    addVariable(mangledName, type, closure.unsafeCast())

    return function
  }
}

fun generateBody(descriptor: ResolvedFunDecl): CompilerContext.(List<Argument>) -> Unit = {
  either.eager<CodegenViolation, Unit> {
    descriptor.content.codegen()

    if (descriptor.returnType != UnitType) return@eager
    if (descriptor.content.filterIsInstance<ResolvedReturnStmt>().isNotEmpty()) return@eager

    buildReturn()
  }
}

fun generateParameter(realParameters: Map<Identifier, PlankType>, context: CompilerContext) =
  fun(index: Int, parameter: Argument) {
    val plankType = realParameters.values.toList().getOrNull(index)
      ?: context.unresolvedTypeError("type of parameter $index")

    val (name) = realParameters.keys.toList().getOrElse(index) {
      context.unresolvedVariableError(parameter.getName())
    }

    context.addVariable(name, plankType, context.alloca(parameter, "parameter.$name"))
  }

fun CompilerContext.addIrClosure(
  name: String,
  type: FunctionType,
  generateBody: CompilerContext.(List<Argument>) -> Unit,
): IRClosure {
  val closure = IRClosure(
    name = name,
    mangledName = name,
    type = type,
    references = LinkedHashMap(),
    returnType = type.actualReturnType,
    realParameters = type.realParameters,
    generateBody = generateBody,
  )

  addFunction(closure)

  return closure
}

fun CompilerContext.addIrClosure(
  descriptor: ResolvedFunDecl,
  generateBody: CompilerContext.(List<Argument>) -> Unit,
): Function = addFunction(
  IRClosure(
    name = descriptor.name.text,
    mangledName = mangleFunction(descriptor),
    type = descriptor.type,
    references = descriptor.references,
    returnType = descriptor.returnType,
    realParameters = descriptor.realParameters,
    generateBody = generateBody,
  )
)

fun CompilerContext.addIrFunction(
  descriptor: ResolvedFunDecl,
  generateBody: (CompilerContext.(List<Argument>) -> Unit)? = null,
): Function = addFunction(
  IRNamedFunction(
    name = descriptor.name.text,
    mangledName = mangleFunction(descriptor),
    type = descriptor.type,
    returnType = descriptor.returnType,
    realParameters = descriptor.realParameters,
    generateBody = generateBody,
  )
)
