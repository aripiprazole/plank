package com.gabrielleeg1.plank.shared

import pw.binom.Stack

// TODO add support for recursive dependencies
fun <T> Graph<T>.depthFirstSearch(root: T): Set<T> {
  if (!hasVertex(root)) return emptySet()

  val visited = linkedSetOf<T>()
  val stack = Stack<T>().apply { pushLast(root) }

  while (!stack.isEmpty) {
    val vertex = stack.popLast()

    visited.add(vertex)

    val edges = values.getOrPut(vertex) { mutableSetOf() }

    edges.forEach(stack::pushFirst)
  }

  return visited
}
