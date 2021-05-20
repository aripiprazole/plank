package com.lorenzoog.plank.compiler.instructions

import com.lorenzoog.plank.analyzer.PlankType
import com.lorenzoog.plank.compiler.CompilerContext
import com.lorenzoog.plank.grammar.element.Expr
import kotlin.reflect.KClass
import org.llvm4j.llvm4j.Function

sealed class CodegenError {
  abstract val context: CompilerContext

  abstract fun render(): String

  data class InvalidFunction(
    val function: Function,
    override val context: CompilerContext
  ) : CodegenError() {
    override fun render(): String =
      "Invalid function ${function.getName()} at ${context.currentFile.location}"
  }

  data class UnresolvedType(
    val name: String,
    override val context: CompilerContext
  ) : CodegenError() {
    override fun render(): String = "Unresolved type $name at ${context.currentFile.location}"
  }

  data class UnresolvedFunction(
    val callee: Expr,
    override val context: CompilerContext
  ) : CodegenError() {
    override fun render(): String = "Unresolved callable at ${callee.location}"
  }

  data class UnresolvedVariable(
    val name: String,
    override val context: CompilerContext
  ) : CodegenError() {
    override fun render(): String = "Unresolved variable $name at ${context.currentFile.location}"
  }

  data class InvalidConstant(
    val value: Any,
    override val context: CompilerContext
  ) : CodegenError() {
    override fun render(): String = "Invalid const $value at ${context.currentFile.location}"
  }

  data class UnresolvedModule(
    val name: String,
    override val context: CompilerContext
  ) : CodegenError() {
    override fun render(): String = "Unresolved module $name at ${context.currentFile.location}"
  }

  data class ExpectedType(
    val expected: KClass<out PlankType>,
    override val context: CompilerContext
  ) : CodegenError() {
    override fun render(): String =
      "Expected type ${expected.simpleName} at ${context.currentFile.location}"
  }

  data class MismatchTypes(
    val actual: PlankType,
    val expected: PlankType,
    override val context: CompilerContext
  ) : CodegenError() {
    override fun render(): String =
      "Mismatch types. Expected $expected, got $actual at ${context.currentFile.location}"
  }

  data class LlvmError(
    val message: String,
    override val context: CompilerContext
  ) : CodegenError() {
    override fun render(): String = "Unknown llvm error. $message"
  }

  data class UnresolvedFieldError(
    val field: String,
    val struct: PlankType,
    override val context: CompilerContext
  ) : CodegenError() {
    override fun render(): String =
      "Unresolved field error $field in type $struct at ${context.currentFile.location}"
  }
}

fun CompilerContext.unresolvedFunctionError(callee: Expr): CodegenError {
  return CodegenError.UnresolvedFunction(callee, this)
}

fun CompilerContext.invalidFunctionError(function: Function): CodegenError {
  return CodegenError.InvalidFunction(function, this)
}

fun CompilerContext.unresolvedVariableError(name: String): CodegenError {
  return CodegenError.UnresolvedVariable(name, this)
}

fun CompilerContext.invalidConstantError(value: Any): CodegenError {
  return CodegenError.InvalidConstant(value, this)
}

fun CompilerContext.unresolvedTypeError(name: String): CodegenError {
  return CodegenError.UnresolvedType(name, this)
}

fun CompilerContext.unresolvedFieldError(name: String, struct: PlankType): CodegenError {
  return CodegenError.UnresolvedFieldError(name, struct, this)
}

fun CompilerContext.expectedTypeError(expected: KClass<out PlankType>): CodegenError {
  return CodegenError.ExpectedType(expected, this)
}

fun CompilerContext.mismatchTypesError(actual: PlankType, expected: PlankType): CodegenError {
  return CodegenError.MismatchTypes(actual, expected, this)
}

fun CompilerContext.unresolvedModuleError(name: String): CodegenError {
  return CodegenError.UnresolvedModule(name, this)
}

fun CompilerContext.llvmError(message: String): CodegenError {
  return CodegenError.LlvmError(message, this)
}
