package com.gabrielleeg1.plank.compiler.instructions.element

import arrow.core.Either
import arrow.core.Either.Right
import arrow.core.computations.either
import arrow.core.identity
import arrow.core.left
import arrow.core.traverseEither
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
import org.llvm4j.llvm4j.BasicBlock
import org.llvm4j.llvm4j.Function
import org.llvm4j.llvm4j.PointerType

abstract class IRFunction : IRElement() {
  abstract val name: String
  abstract val mangledName: String

  /** Access the function in the [context] */
  abstract fun accessIn(context: CompilerContext): AllocaInstruction?

  /** Generates the function in the [this] */
  abstract override fun CompilerContext.codegen(): Either<CodegenViolation, Function>
}

class IRNamedFunction(
  val type: FunctionType,
  override val name: String,
  override val mangledName: String,
  private val returnType: PlankType,
  private val realParameters: Map<Identifier, PlankType>,
  private val generateBody: (CompilerContext.(List<Argument>) -> Unit)? = null,
) : IRFunction() {
  override fun accessIn(context: CompilerContext): AllocaInstruction? {
    return context.module
      .getFunction(mangledName)
      .map { context.alloca(it, "function_access_$name") }
      .toNullable()
  }

  override fun CompilerContext.codegen(): Either<CodegenViolation, Function> = either.eager {
    val function = module.addFunction(
      mangledName,
      context.getFunctionType(
        returnType = returnType.convertType().bind(),
        *realParameters.values
          .map { type ->
            type.cast<FunctionType>()?.copy(isClosure = true)?.convertType()
              ?.map { if (it is PointerType) it else context.getPointerType(it).unwrap() }?.bind()
              ?: type.convertType().bind()
          }
          .toTypedArray(),
        isVariadic = false,
      ),
    )

    if (generateBody == null) return@eager function

    val enclosingBlock = insertionBlock

    createNestedScope(name) {
      builder.positionAfter(context.newBasicBlock("entry").also(function::addBasicBlock))

      function.getParameters()
        .mapIndexed(generateParameter(realParameters, this))
        .traverseEither(::identity).bind()

      generateBody.invoke(this, function.getParameters().toList())

      ensure(function.verify()) { invalidFunctionError(function) }
    }

    if (enclosingBlock is Right<BasicBlock>) {
      builder.positionAfter(enclosingBlock.value)
    }

    function
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
) : IRFunction() {
  override fun accessIn(context: CompilerContext): AllocaInstruction {
    return context.findVariableAlloca(mangledName)!!
  }

  override fun CompilerContext.codegen(): Either<CodegenViolation, Function> = either.eager {
    val references = references.mapKeys { (name) -> name.text }

    val environmentType = context.getNamedStructType("Closure_${mangledName}_Environment").apply {
      setElementTypes(
        *references.map { it.value.convertType().bind() }.toTypedArray(),
        isPacked = false
      )
    }

    val functionType = context.getFunctionType(
      returnType.convertType().bind(),
      pointerType(environmentType),
      *realParameters.values.toList().map { type -> type.convertType().bind() }.toTypedArray(),
    )

    val closureFunctionType = context.getNamedStructType("Closure_${mangledName}_Function").apply {
      setElementTypes(
        pointerType(functionType),
        pointerType(environmentType),
        isPacked = false
      )
    }

    val function = module.addFunction(mangledName, functionType)

    val enclosingBlock = insertionBlock.bind() // All closures are nested

    createNestedScope(name) {
      builder.positionAfter(context.newBasicBlock("entry").also(function::addBasicBlock))

      val environment = function.getParameter(0).unwrap().apply {
        setName("closure_environment")
      }

      references.entries.forEachIndexed { index, (reference, type) ->
        val variable = alloca(buildLoad(getField(environment, index).bind()), "ENV.$reference")

        addVariable(reference, type, variable)
      }

      val parameters = function.getParameters().drop(1)

      parameters
        .mapIndexed(generateParameter(realParameters, this))
        .traverseEither(::identity).bind()

      generateBody.invoke(this, parameters)

      ensure(function.verify()) { invalidFunctionError(function) }
    }

    builder.positionAfter(enclosingBlock)

    val variables = references.keys
      .mapNotNull { findVariableAlloca(it) }
      .map { buildLoad(it) }
      .toTypedArray()

    val environment = getInstance(environmentType, *variables, isPointer = true).bind()
    val closure = getInstance(closureFunctionType, function, environment, isPointer = true).bind()

    addVariable(mangledName, type, closure.unsafeCast())

    function
  }
}

fun generateBody(descriptor: ResolvedFunDecl): CompilerContext.(List<Argument>) -> Unit = {
  either.eager<CodegenViolation, Unit> {
    descriptor.content.map { it.codegen().bind() }

    if (descriptor.returnType != UnitType) return@eager
    if (descriptor.content.filterIsInstance<ResolvedReturnStmt>().isNotEmpty()) return@eager

    buildReturn()
  }
}

fun generateParameter(realParameters: Map<Identifier, PlankType>, context: CompilerContext) =
  fun(index: Int, parameter: Argument): Either<CodegenViolation, Unit> = either.eager {
    val plankType = realParameters.values.toList().getOrNull(index)
      ?: context.unresolvedTypeError("type of parameter $index").left().bind<PlankType>()

    val (name) = realParameters.keys.toList().getOrElse(index) {
      context.unresolvedVariableError(parameter.getName()).left().bind<Identifier>()
    }

    context.addVariable(name, plankType, context.alloca(parameter, "parameter.$name"))
  }

fun CompilerContext.addIrClosure(
  name: String,
  type: FunctionType,
  generateBody: CompilerContext.(List<Argument>) -> Unit,
): Either<CodegenViolation, IRClosure> = either.eager {
  val closure = IRClosure(
    name = name,
    mangledName = name,
    type = type,
    references = LinkedHashMap(),
    returnType = type.actualReturnType,
    realParameters = type.realParameters,
    generateBody = generateBody,
  )

  addFunction(closure).bind()

  closure
}

fun CompilerContext.addIrClosure(
  descriptor: ResolvedFunDecl,
  generateBody: CompilerContext.(List<Argument>) -> Unit,
): Either<CodegenViolation, Function> = addFunction(
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
): Either<CodegenViolation, Function> = addFunction(
  IRNamedFunction(
    name = descriptor.name.text,
    mangledName = mangleFunction(descriptor),
    type = descriptor.type,
    returnType = descriptor.returnType,
    realParameters = descriptor.realParameters,
    generateBody = generateBody,
  )
)
