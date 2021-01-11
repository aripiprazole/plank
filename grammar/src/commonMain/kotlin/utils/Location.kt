package com.lorenzoog.jplank.utils

import com.lorenzoog.jplank.element.Location
import org.antlr.v4.kotlinruntime.Token

fun Token?.location(filePath: String): Location {
  return Location(this?.line ?: -1, this?.charPositionInLine ?: -1, filePath)
}
