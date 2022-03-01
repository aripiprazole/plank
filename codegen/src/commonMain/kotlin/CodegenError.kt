package org.plank.codegen

class CodegenError(message: String, val context: CodegenContext) :
  Exception("$message at $context")

fun CodegenContext.codegenError(message: String): Nothing {
  throw CodegenError(message, this)
}
