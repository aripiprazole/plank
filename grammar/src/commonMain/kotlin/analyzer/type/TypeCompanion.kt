package com.lorenzoog.jplank.analyzer.type

interface TypeCompanion {
  fun isAssignableBy(another: PkType): Boolean
}
