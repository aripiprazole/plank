package com.lorenzoog.jplank.element

data class Location(val line: Int, val column: Int = 0, val filePath: String) {
  override fun toString(): String {
    return "$filePath ($line, $column)"
  }
}
