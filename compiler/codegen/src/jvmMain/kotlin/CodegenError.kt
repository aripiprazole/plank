package org.plank.codegen

import org.plank.codegen.scope.CodegenCtx

class CodegenError(message: String, val context: CodegenCtx) :
  Exception("$message at $context")

fun CodegenCtx.codegenError(message: String): Nothing {
  throw CodegenError(message, this)
}
