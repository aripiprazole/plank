package org.plank.codegen

import org.plank.codegen.scope.CodegenContext

class CodegenError(message: String, val context: CodegenContext) :
  Exception("$message at $context")

fun CodegenContext.codegenError(message: String): Nothing {
  throw CodegenError(message, this)
}
