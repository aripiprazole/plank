package com.gabrielleeg1.plank.compiler

import org.plank.llvm4k.Context
import org.plank.llvm4k.Module

sealed interface CodegenContext : Context {
  val currentModule: Module
}
