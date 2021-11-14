package com.gabrielleeg1.plank.grammar.element

interface ErrorPlankElement : PlankElement {
  val message: String
  val arguments: List<Any>
}
