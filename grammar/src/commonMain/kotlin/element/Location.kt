package com.lorenzoog.plank.grammar.element

data class Location(
  val line: Int,
  val column: Int = 0,
  val file: PlankFile
) {
  override fun toString(): String {
    return "${file.path} ($line, $column)"
  }
}
