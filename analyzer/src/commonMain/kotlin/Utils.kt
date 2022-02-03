package org.plank.analyzer

import org.plank.grammar.element.TypeRef
import kotlin.jvm.JvmName

@JvmName("visitTypeReferenceOrElse")
inline fun TypeRef.Visitor<PlankType>.visit(
  typeDef: TypeRef?,
  orElse: () -> PlankType
): PlankType {
  return typeDef?.let { visit(it) } ?: orElse()
}
