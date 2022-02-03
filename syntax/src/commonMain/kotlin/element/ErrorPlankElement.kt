package org.plank.syntax.element

interface ErrorPlankElement : PlankElement {
  val message: String
  val arguments: List<Any>
}
