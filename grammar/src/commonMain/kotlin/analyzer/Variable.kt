package com.lorenzoog.jplank.analyzer

import com.lorenzoog.jplank.analyzer.type.PlankType

data class Variable(
  val mutable: Boolean,
  val type: PlankType
)
