package com.lorenzoog.jplank.utils

import com.lorenzoog.jplank.element.Location
import com.lorenzoog.jplank.element.PlankFile
import org.antlr.v4.kotlinruntime.Token

fun Token?.location(file: PlankFile): Location {
  return Location(this?.line ?: -1, this?.charPositionInLine ?: -1, file)
}
