package com.lorenzoog.jplank.analyzer

import com.lorenzoog.jplank.analyzer.type.PkType

data class Variable(
  val mutable: Boolean,
  val type: PkType
)
