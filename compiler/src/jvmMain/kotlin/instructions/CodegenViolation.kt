package com.gabrielleeg1.plank.compiler.instructions

import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.element.TypedExpr
import com.gabrielleeg1.plank.compiler.CompilerContext
import com.gabrielleeg1.plank.grammar.message.CompilerLogger
import org.llvm4j.llvm4j.Function
import kotlin.reflect.KClass

class CodegenViolation(
  override val message: String,
  val context: CompilerContext,
) : RuntimeException() {
  fun render(logger: CompilerLogger) {
    logger.severe(message, context.location)
  }
}

fun CompilerContext.unresolvedFunctionError(callee: TypedExpr): Nothing {
  throw CodegenViolation("Unresolved function `$callee`", this)
}

fun CompilerContext.invalidFunctionError(function: Function): Nothing {
  throw CodegenViolation("Invalid function `${function.getName()}`", this)
}

fun CompilerContext.unresolvedVariableError(name: String): Nothing {
  throw CodegenViolation("Unresolved variable `$name`", this)
}

fun CompilerContext.invalidConstantError(value: Any): Nothing {
  throw CodegenViolation("Invalid constant $value", this)
}

fun CompilerContext.unresolvedTypeError(name: String): Nothing {
  throw CodegenViolation("Unresolved type `$name`", this)
}

fun CompilerContext.unresolvedFieldError(name: String, struct: PlankType): Nothing {
  throw CodegenViolation("Unresolved property error `$name` in type $struct", this)
}

fun CompilerContext.expectedTypeError(expected: KClass<out PlankType>): Nothing {
  throw CodegenViolation("Expected type `${expected.simpleName}`", this)
}

fun CompilerContext.mismatchTypesError(actual: PlankType, expected: PlankType): Nothing {
  throw CodegenViolation("Mismatch types. expected $expected, but got $actual", this)
}

fun CompilerContext.unresolvedModuleError(name: String): Nothing {
  throw CodegenViolation("Unresolved module `$name`", this)
}

fun CompilerContext.llvmError(message: String): Nothing {
  throw CodegenViolation(message, this)
}
