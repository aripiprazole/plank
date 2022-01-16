package com.gabrielleeg1.plank.compiler.instructions

import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.grammar.message.CompilerLogger
import org.llvm4j.llvm4j.Function
import kotlin.reflect.KClass

sealed class CodegenViolation {
  abstract val context: CompilerContext

  abstract fun render(logger: CompilerLogger)

  data class InvalidFunction(
    val function: Function,
    override val context: CompilerContext
  ) : CodegenViolation() {
    override fun render(logger: CompilerLogger) {
      logger.severe("Invalid function ${function.getName()} at ${context.currentFile.location}")
      logger.severe(context.module.getAsString())
    }
  }

  data class UnresolvedType(
    val name: String,
    override val context: CompilerContext
  ) : CodegenViolation() {
    override fun render(logger: CompilerLogger) {
      logger.severe("Unresolved type $name at ${context.currentFile.location}")
    }
  }

  data class UnresolvedFunction(
    val callee: TypedExpr,
    override val context: CompilerContext
  ) : CodegenViolation() {
    override fun render(logger: CompilerLogger) {
      logger.severe("Unresolved callable $callee at ${callee.location}")
    }
  }

  data class UnresolvedVariable(
    val name: String,
    override val context: CompilerContext
  ) : CodegenViolation() {
    override fun render(logger: CompilerLogger) {
      logger.severe("Unresolved variable $name at ${context.currentFile.location}")
    }
  }

  data class InvalidConstant(
    val value: Any,
    override val context: CompilerContext
  ) : CodegenViolation() {
    override fun render(logger: CompilerLogger) {
      logger.severe("Invalid const $value at ${context.currentFile.location}")
    }
  }

  data class UnresolvedModule(
    val name: String,
    override val context: CompilerContext
  ) : CodegenViolation() {
    override fun render(logger: CompilerLogger) {
      logger.severe("Unresolved module $name at ${context.currentFile.location}")
    }
  }

  data class ExpectedType(
    val expected: KClass<out PlankType>,
    override val context: CompilerContext
  ) : CodegenViolation() {
    override fun render(logger: CompilerLogger) {
      logger.severe("Expected type ${expected.simpleName} at ${context.currentFile.location}")
    }
  }

  data class MismatchTypes(
    val actual: PlankType,
    val expected: PlankType,
    override val context: CompilerContext
  ) : CodegenViolation() {
    override fun render(logger: CompilerLogger) {
      logger.severe("Mismatch types. Expected $expected, got $actual at ${context.currentFile.location}")
    }
  }

  data class LlvmViolation(
    val message: String,
    override val context: CompilerContext
  ) : CodegenViolation() {
    override fun render(logger: CompilerLogger) {
      logger.severe("Unknown llvm error. $message")
    }
  }

  data class UnresolvedFieldViolation(
    val field: String,
    val struct: PlankType,
    override val context: CompilerContext
  ) : CodegenViolation() {
    override fun render(logger: CompilerLogger) {
      logger.severe("Unresolved field error $field in type $struct at ${context.currentFile.location}")
    }
  }
}

fun CompilerContext.unresolvedFunctionError(callee: TypedExpr): CodegenViolation {
  return CodegenViolation.UnresolvedFunction(callee, this)
}

fun CompilerContext.invalidFunctionError(function: Function): CodegenViolation {
  return CodegenViolation.InvalidFunction(function, this)
}

fun CompilerContext.unresolvedVariableError(name: String): CodegenViolation {
  return CodegenViolation.UnresolvedVariable(name, this)
}

fun CompilerContext.invalidConstantError(value: Any): CodegenViolation {
  return CodegenViolation.InvalidConstant(value, this)
}

fun CompilerContext.unresolvedTypeError(name: String): CodegenViolation {
  return CodegenViolation.UnresolvedType(name, this)
}

fun CompilerContext.unresolvedFieldError(name: String, struct: PlankType): CodegenViolation {
  return CodegenViolation.UnresolvedFieldViolation(name, struct, this)
}

fun CompilerContext.expectedTypeError(expected: KClass<out PlankType>): CodegenViolation {
  return CodegenViolation.ExpectedType(expected, this)
}

fun CompilerContext.mismatchTypesError(actual: PlankType, expected: PlankType): CodegenViolation {
  return CodegenViolation.MismatchTypes(actual, expected, this)
}

fun CompilerContext.unresolvedModuleError(name: String): CodegenViolation {
  return CodegenViolation.UnresolvedModule(name, this)
}

fun CompilerContext.llvmError(message: String): CodegenViolation {
  return CodegenViolation.LlvmViolation(message, this)
}
