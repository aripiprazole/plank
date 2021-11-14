package com.gabrielleeg1.plank.analyzer

import com.gabrielleeg1.plank.grammar.element.TypeRef
import kotlin.jvm.JvmName

@JvmName("visitTypeReferenceOrElse")
inline fun TypeRef.Visitor<PlankType>.visit(
    typeDef: TypeRef?,
    orElse: () -> PlankType
): PlankType {
  return typeDef?.let { visit(it) } ?: orElse()
}
