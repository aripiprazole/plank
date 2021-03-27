package com.lorenzoog.plank.grammar.utils

import com.lorenzoog.plank.grammar.element.Location
import com.lorenzoog.plank.grammar.element.PlankFile
import org.antlr.v4.kotlinruntime.Token

fun Token?.location(file: PlankFile): Location {
  return Location(this?.line ?: -1, this?.charPositionInLine ?: -1, file)
}
