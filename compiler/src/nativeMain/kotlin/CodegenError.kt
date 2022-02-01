package com.gabrielleeg1.plank.compiler

class CodegenError(override val message: String, val context: CodegenContext) : Exception(message)

fun CodegenContext.codegenError(message: String): Nothing {
  throw CodegenError(message, this)
}
