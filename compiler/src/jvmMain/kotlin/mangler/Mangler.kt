package com.lorenzoog.jplank.compiler.mangler

import com.lorenzoog.jplank.compiler.PlankContext
import com.lorenzoog.jplank.element.Decl

interface Mangler {
  fun mangle(context: PlankContext, function: Decl.FunDecl): String
}
