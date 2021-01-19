package com.lorenzoog.jplank.analyzer.type

interface TypeCompanion {
  infix fun isAssignableBy(another: PlankType): Boolean
}
