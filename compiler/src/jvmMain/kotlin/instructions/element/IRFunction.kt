package com.gabrielleeg1.plank.compiler.instructions.element

import arrow.core.identity
import com.gabrielleeg1.plank.analyzer.FunctionType
import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.UnitType
import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.analyzer.element.ResolvedReturnStmt
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.compiler.ExecutionContext
import com.gabrielleeg1.plank.compiler.builder.alloca
import com.gabrielleeg1.plank.compiler.builder.buildBitcast
import com.gabrielleeg1.plank.compiler.builder.buildCall
import com.gabrielleeg1.plank.compiler.builder.buildLoad
import com.gabrielleeg1.plank.compiler.builder.buildReturn
import com.gabrielleeg1.plank.compiler.builder.getField
import com.gabrielleeg1.plank.compiler.builder.getInstance
import com.gabrielleeg1.plank.compiler.builder.insertionBlock
import com.gabrielleeg1.plank.compiler.builder.pointerType
import com.gabrielleeg1.plank.compiler.createScopeContext
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
  private val generateBody: ExecutionContext.(List<Argument>) -> Unit,
) : IRFunction {

  private val parameters = type.realParameters.entries.toList().map { it.toPair() }
  private val references = parameters.toList().dropLast(1).associate(::identity)

  private fun generateNesting(
    index: Int,
    builder: ExecutionContext.(PlankType, List<Argument>) -> Unit = { _, arguments ->
      generateBody(arguments)
    }
  ): IRClosure {
    val type = FunctionType(
      parameters[index].second,
      when (val returnType = type.nest(index)) {
        is FunctionType -> returnType.copy(isClosure = true)
        else -> returnType
      }
    )

    val mangledName = "$mangledName-$index"

    return IRClosure(
      name = mangledName,
      mangledName = "$mangledName-{{closure}}",
      type = type,
      references = references,
      returnType = type.returnType,
      realParameters = mapOf(parameters[index]),
      generateBody = { builder(type.returnType, it) },
    )
  }

  override fun accessIn(context: CompilerContext): AllocaInstruction? {
    return context.module
      .getFunction(mangledName)
      .map { context.alloca(context.buildCall(it), "function_access_$name") }
      .toNullable()
  }

  override fun CompilerContext.codegen(): Function {
    val reversedParameters = type.realParameters.keys

    val closureReturnType = type.typegen()

    val enclosingBlock = runCatching { insertionBlock }
    val toplevelFunction = context
      .getFunctionType(closureReturnType)
      .let { module.addFunction(mangledName, it) }

    val entry = context.newBasicBlock("entry").also(toplevelFunction::addBasicBlock)

    createScopeContext(name) {
      builder.positionAfter(entry)

      val closure = if (parameters.isNotEmpty()) {
        List(parameters.size - 1, ::identity)
          .reversed()
          .fold(generateNesting(reversedParameters.size - 1)) { acc, i ->
            generateNesting(i) { returnType, _ ->
              val closure = acc.also { it.codegen() }.accessIn(this)

              if (returnType == UnitType) {
                buildReturn()
              } else {
                val closureType = returnType.cast<FunctionType>()!!.copy(isClosure = true).typegen()

                buildReturn(buildBitcast(closure, closureType))
              }
            }
          }
          .also { it.codegen() }
          .accessIn(this)
      } else {
        addIrClosure(name, type, generateBody).also { it.codegen() }.accessIn(this)
      }

      builder.positionAfter(entry)

      buildReturn(buildBitcast(closure, closureReturnType))

      if (!toplevelFunction.verify()) {
        invalidFunctionError(toplevelFunction)
      }
    }

    if (enclosingBlock.isSuccess) {
      builder.positionAfter(enclosingBlock.getOrThrow())
    }

    return toplevelFunction
  }
}

class IRClosure(
  val type: FunctionType,
  override val name: String,
  override val mangledName: String,
  private val references: Map<Identifier, PlankType>,
  private val returnType: PlankType,
  private val realParameters: Map<Identifier, PlankType>,
  private val generateBody: ExecutionContext.(List<Argument>) -> Unit,
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

      val executionContext = ExecutionContext(this)

      with(executionContext) {
        references.entries.forEachIndexed { index, (reference, type) ->
          val variable = alloca(buildLoad(getField(environment, index)), "ENV.$reference")

          parameters[reference] = variable
          addVariable(reference, type, variable)
        }

        val parameters = function.getParameters().drop(1)

        parameters
          .forEachIndexed(generateParameter(realParameters))

        generateBody(parameters)
      }

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

fun generateBody(descriptor: ResolvedFunDecl): ExecutionContext.(List<Argument>) -> Unit =
  fun CompilerContext.(_: List<Argument>) {
    descriptor.content.codegen()

    if (descriptor.returnType != UnitType) return
    if (descriptor.content.filterIsInstance<ResolvedReturnStmt>().isNotEmpty()) return

    buildReturn()
  }

fun ExecutionContext.generateParameter(realParameters: Map<Identifier, PlankType>) =
  fun(index: Int, argument: Argument) {
    val plankType = realParameters.values.toList().getOrNull(index)
      ?: unresolvedTypeError("type of parameter $index")

    val (name) = realParameters.keys.toList().getOrElse(index) {
      unresolvedVariableError(argument.getName())
    }

    parameters[name] = argument
    addVariable(name, plankType, alloca(argument, "parameter.$name"))
  }

fun CompilerContext.addIrClosure(
  name: String,
  type: FunctionType,
  generateBody: ExecutionContext.(List<Argument>) -> Unit,
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
  generateBody: ExecutionContext.(List<Argument>) -> Unit,
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
  generateBody: ExecutionContext.(List<Argument>) -> Unit,
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
