package com.gabrielleeg1.plank.compiler.instructions

import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.grammar.message.CompilerLogger
import org.llvm4j.llvm4j.Function
import kotlin.reflect.KClass

sealed interface CodegenViolation {
  val context: CompilerContext
  val message: String

  fun render(logger: CompilerLogger)
}

data class InvalidFunction(
  val function: Function,
  override val context: CompilerContext
) : CodegenViolation {
  override val message: String = "Invalid function %s"

  override fun render(logger: CompilerLogger) {
    logger.severe(message.format(function.getName()), context.location)
    logger.debug(context.module.getAsString())
  }
}

data class UnresolvedTypeViolation(
  val name: String,
  override val context: CompilerContext
) : CodegenViolation {
  override val message: String = "Unresolved type %s"

  override fun render(logger: CompilerLogger) {
    logger.severe(message.format(name), context.location)
  }
}

data class UnresolvedFunctionViolation(
  val callee: TypedExpr,
  override val context: CompilerContext
) : CodegenViolation {
  override val message: String = "Unresolved callable %s"

  override fun render(logger: CompilerLogger) {
    logger.severe(message.format(callee), callee.location)
  }
}

data class UnresolvedVariableViolation(
  val name: String,
  override val context: CompilerContext
) : CodegenViolation {
  override val message: String = "Unresolved variable %s"

  override fun render(logger: CompilerLogger) {
    logger.severe(message.format(name), context.location)
  }
}

data class InvalidConstantViolation(
  val value: Any,
  override val context: CompilerContext
) : CodegenViolation {
  override val message: String = "Invalid const %s"

  override fun render(logger: CompilerLogger) {
    logger.severe(message.format(value), context.location)
  }
}

data class UnresolvedModuleViolation(
  val name: String,
  override val context: CompilerContext
) : CodegenViolation {
  override val message: String = "Unresolved module %s"

  override fun render(logger: CompilerLogger) {
    logger.severe(message.format(name), context.location)
  }
}

data class ExpectedType(
  val expected: KClass<out PlankType>,
  override val context: CompilerContext
) : CodegenViolation {
  override val message: String = "Expected type %s"

  override fun render(logger: CompilerLogger) {
    logger.severe(message.format(expected.simpleName), context.location)
  }
}

data class MismatchTypes(
  val actual: PlankType,
  val expected: PlankType,
  override val context: CompilerContext
) : CodegenViolation {
  override val message: String = "Mismatch types. Expected %s, got %s"

  override fun render(logger: CompilerLogger) {
    logger.severe(message.format(expected, actual), context.location)
  }
}

data class LlvmViolation(
  val value: String,
  override val context: CompilerContext
) : CodegenViolation {
  override val message: String = "Unknown llvm error. %s"

  override fun render(logger: CompilerLogger) {
    logger.severe(message.format(value))
  }
}

data class UnresolvedFieldViolation(
  val field: String,
  val struct: PlankType,
  override val context: CompilerContext
) : CodegenViolation {
  override val message: String = "Unresolved field error %s in type %s"

  override fun render(logger: CompilerLogger) {
    logger.severe(message.format(field, struct), context.location)
  }
}

fun CompilerContext.unresolvedFunctionError(callee: TypedExpr): CodegenViolation {
  return UnresolvedFunctionViolation(callee, this)
}

fun CompilerContext.invalidFunctionError(function: Function): CodegenViolation {
  return InvalidFunction(function, this)
}

fun CompilerContext.unresolvedVariableError(name: String): CodegenViolation {
  return UnresolvedVariableViolation(name, this)
}

fun CompilerContext.invalidConstantError(value: Any): CodegenViolation {
  return InvalidConstantViolation(value, this)
}

fun CompilerContext.unresolvedTypeError(name: String): CodegenViolation {
  return UnresolvedTypeViolation(name, this)
}

fun CompilerContext.unresolvedFieldError(name: String, struct: PlankType): CodegenViolation {
  return UnresolvedFieldViolation(name, struct, this)
}

fun CompilerContext.expectedTypeError(expected: KClass<out PlankType>): CodegenViolation {
  return ExpectedType(expected, this)
}

fun CompilerContext.mismatchTypesError(actual: PlankType, expected: PlankType): CodegenViolation {
  return MismatchTypes(actual, expected, this)
}

fun CompilerContext.unresolvedModuleError(name: String): CodegenViolation {
  return UnresolvedModuleViolation(name, this)
}

fun CompilerContext.llvmError(message: String): CodegenViolation {
  return LlvmViolation(message, this)
}
