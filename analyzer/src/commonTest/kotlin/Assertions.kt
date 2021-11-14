package com.gabrielleeg1.plank.analyzer.test

import com.gabrielleeg1.plank.analyzer.PlankType
import com.gabrielleeg1.plank.analyzer.ResolutionException
import com.gabrielleeg1.plank.analyzer.element.ResolvedPlankElement
import com.gabrielleeg1.plank.analyzer.element.TypedPlankElement
import com.gabrielleeg1.plank.grammar.element.ErrorPlankElement
import kotlin.test.assertEquals

fun assertNotViolated(node: ResolvedPlankElement) {
  if (node !is ErrorPlankElement) return

  throw ResolutionException(node.message) // TODO
}

fun assertViolation(node: ResolvedPlankElement, message: String) {
  if (node !is ErrorPlankElement) {
    throw AssertionError("Got node of type ${node::class.simpleName}, expecting to be ViolatedPlankElement")
  }

  assertEquals(
    node.message, message,
    "Element ${node::class.simpleName} has unexpected message ${node.message}, expecting $message"
  )
}

fun assertType(node: TypedPlankElement, type: PlankType) {
  assertEquals(
    node.type, type,
    "Element ${node::class.simpleName} has unexpected type ${node.type}, expecting $type"
  )
}
