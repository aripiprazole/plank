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
import org.llvm4j.llvm4j.Value

interface IRFunction : IRElement {
  val name: String
  val mangledName: String

  /** Access the function in the [context] */
  fun accessIn(context: CompilerContext): AllocaInstruction?

  /** Generates the function in the [this] */
  override fun CompilerContext.codegen(): Value
}

class IRCurried(
  val type: FunctionType,
  val nested: Boolean,
  val variableReferences: Map<Identifier, PlankType>,
  override val name: String,
  override val mangledName: String,
  private val returnType: PlankType,
  private val realParameters: Map<Identifier, PlankType>,
  private val generateBody: ExecutionContext.(List<Argument>) -> Unit,
) : IRFunction {
  private val parameters = type.realParameters.entries.toList().map { it.toPair() }
  private val references =
    variableReferences + parameters.toList().dropLast(1).associate(::identity)

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

  override fun CompilerContext.codegen(): Value {
    val reversedParameters = type.realParameters.keys
    val closure: Value

    createScopeContext(name) {
      closure = if (parameters.isNotEmpty()) {
        List(parameters.size - 1, ::identity)
          .reversed()
          .fold(generateNesting(reversedParameters.size - 1)) { acc, i ->
            generateNesting(i) { returnType, _ ->
              val func = acc.also { it.codegen() }.accessIn(this)

              if (returnType == UnitType) {
                buildReturn()
              } else {
                val closureType = returnType.cast<FunctionType>()!!.copy(isClosure = true).typegen()

                buildReturn(buildBitcast(func, closureType))
              }
            }
          }
          .also { it.codegen() }
          .accessIn(this)
      } else {
        addIrClosure(this@IRCurried.name, type, references, generateBody)
          .also { it.codegen() }
          .accessIn(this)
      }
    }

    if (nested) {
      addVariable(this@IRCurried.name, type, closure.unsafeCast())
    }

    return closure
  }
}

class IRGlobalFunction(
  override val mangledName: String,
  val descriptor: ResolvedFunDecl,
  private val generateBody: ExecutionContext.(List<Argument>) -> Unit,
  override val name: String = descriptor.name.text,
) : IRFunction {
  override fun accessIn(context: CompilerContext): AllocaInstruction? {
    return context.module
      .getFunction(mangledName)
      .map { context.alloca(context.buildCall(it), "function_access_$name") }
      .toNullable()
  }

  override fun CompilerContext.codegen(): Value {
    val closureReturnType = descriptor.type.typegen()

    val enclosingBlock = runCatching { insertionBlock }
    val toplevelFunction = context
      .getFunctionType(closureReturnType)
      .let { module.addFunction(mangledName, it) }

    val entry = context.newBasicBlock("entry").also(toplevelFunction::addBasicBlock)

    builder.positionAfter(entry)

    val closure = addIrCurriedFunction(descriptor, false, generateBody)

    builder.positionAfter(entry)

    buildReturn(buildBitcast(closure, closureReturnType))

    if (!toplevelFunction.verify()) {
      invalidFunctionError(toplevelFunction)
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
  private val realParameters: Map<Identifier, PlankType>,
  private val generateBody: ExecutionContext.(List<Argument>) -> Unit,
  private val descriptor: ResolvedFunDecl? = null,
) : IRFunction {
  override fun accessIn(context: CompilerContext): AllocaInstruction {
    return context.findAlloca(mangledName)!!
  }

  override fun CompilerContext.codegen(): Value {
    val returnType = type.actualReturnType.typegen()
    val references = references.mapKeys { (name) -> name.text }

    val environmentType = context.getNamedStructType("Closure_${mangledName}_Environment").apply {
      setElementTypes(
        *references.map { it.value.typegen() }.toTypedArray(),
        isPacked = false
      )
    }

    val functionType = context.getFunctionType(
      returnType,
      pointerType(environmentType),
      *realParameters.values
        .toList()
        .map { type -> type.typegen() }
        .toTypedArray(),
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

      val executionContext = ExecutionContext(this, returnType)

      with(executionContext) {
        references.entries.forEachIndexed { index, (reference, type) ->
          val variable = alloca(buildLoad(getField(environment, index)), "ENV.$reference")

          if (reference in realParameters.keys.map { it.text }) {
            parameters[reference] = variable
          }

          addVariable(reference, type, variable)
        }

        val parameters = function.getParameters().drop(1)

        parameters.forEachIndexed(generateParameter(realParameters))

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

    return closure
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
    argument.setName(name)

    if (plankType.isClosure) {
      addVariable(name, plankType, argument.unsafeCast())
    } else {
      addVariable(name, plankType, alloca(argument, "parameter.$name"))
    }
  }

fun CompilerContext.addGlobalFunction(
  descriptor: ResolvedFunDecl,
  generateBody: ExecutionContext.(List<Argument>) -> Unit = generateBody(descriptor),
): Value {
  return addFunction(IRGlobalFunction(mangleFunction(descriptor), descriptor, generateBody))
}

fun CompilerContext.addIrClosure(
  name: String,
  type: FunctionType,
  references: Map<Identifier, PlankType> = LinkedHashMap(),
  generateBody: ExecutionContext.(List<Argument>) -> Unit,
): IRClosure {
  val closure = IRClosure(
    name = name,
    mangledName = name,
    type = type,
    references = references,
    realParameters = type.realParameters,
    generateBody = generateBody,
  )

  addFunction(closure)

  return closure
}

fun CompilerContext.addIrClosure(
  descriptor: ResolvedFunDecl,
  generateBody: ExecutionContext.(List<Argument>) -> Unit,
): Value = addFunction(
  IRClosure(
    name = descriptor.name.text,
    mangledName = mangleFunction(descriptor),
    type = descriptor.type,
    references = descriptor.references,
    realParameters = descriptor.realParameters,
    generateBody = generateBody,
  )
)

fun CompilerContext.addIrCurriedFunction(
  descriptor: ResolvedFunDecl,
  nested: Boolean = false,
  generateBody: ExecutionContext.(List<Argument>) -> Unit,
): Value = addFunction(
  IRCurried(
    name = descriptor.name.text,
    mangledName = mangleFunction(descriptor),
    type = descriptor.type,
    returnType = descriptor.returnType,
    realParameters = descriptor.realParameters,
    generateBody = generateBody,
    nested = nested,
    variableReferences = descriptor.references,
  )
)
