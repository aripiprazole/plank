package org.plank.codegen.scope

import org.plank.analyzer.infer.Subst
import org.plank.llvm4k.ir.Function
import org.plank.llvm4k.ir.Type
import org.plank.llvm4k.ir.Value

class ExecCtx(
  override val enclosing: ScopeCtx,
  val function: Function,
  val returnType: Type,
  val arguments: MutableMap<String, Value> = linkedMapOf(),
  override val subst: Subst = enclosing.subst,
) : CodegenCtx by enclosing
