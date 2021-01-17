package com.lorenzoog.jplank.element

data class Location(
  val line: Int,
  val column: Int = 0,
  val file: PkFile
) {
  override fun toString(): String {
    return "${file.path} ($line, $column)"
  }
}
