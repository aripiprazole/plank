package com.lorenzoog.plank.analyzer

import com.lorenzoog.plank.grammar.element.TypeDef
import kotlin.jvm.JvmName

@JvmName("visitPkTypeTypeDefNullable")
inline fun TypeDef.Visitor<PlankType>.visit(
  typeDef: TypeDef?,
  orElse: () -> PlankType = { Builtin.Void }
): PlankType {
  return typeDef?.let { visit(it) } ?: orElse()
}
