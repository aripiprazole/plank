package com.lorenzoog.plank.grammar.element

interface ErrorVisitor<T> {
  fun visitErrorElement(error: ErrorPlankElement): T
}
