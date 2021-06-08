package com.lorenzoog.plank.analyzer.element

interface ViolatedPlankElement : ResolvedPlankElement {
  val message: String
  val arguments: List<Any>
}
