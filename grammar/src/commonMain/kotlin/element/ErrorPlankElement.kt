package com.lorenzoog.plank.grammar.element

interface ErrorPlankElement : PlankElement {
  val message: String
  val arguments: List<Any>
}
