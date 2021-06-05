package com.lorenzoog.plank.analyzer

import com.lorenzoog.plank.grammar.element.TypeReference
import kotlin.jvm.JvmName

@JvmName("visitTypeReferenceOrElse")
inline fun TypeReference.Visitor<PlankType>.visit(
  typeDef: TypeReference?,
  orElse: () -> PlankType
): PlankType {
  return typeDef?.let { visit(it) } ?: orElse()
}
