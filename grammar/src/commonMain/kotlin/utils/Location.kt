package com.lorenzoog.jplank.utils

import com.lorenzoog.jplank.element.Location
import com.lorenzoog.jplank.element.PkFile
import org.antlr.v4.kotlinruntime.Token

fun Token?.location(file: PkFile): Location {
  return Location(this?.line ?: -1, this?.charPositionInLine ?: -1, file)
}
