package org.plank.shared

// TODO add support for recursive dependencies
fun <T> Graph<T>.depthFirstSearch(root: T): Set<T> {
  if (!hasVertex(root)) return emptySet()

  val visited = linkedSetOf<T>()
  val stack = LinkedHashSet<T>().apply { add(root) }

  while (!stack.isEmpty()) {
    val vertex = stack.pop()

    visited.add(vertex)

    val edges = values.getOrPut(vertex) { mutableSetOf() }

    edges.forEach(stack::add)
  }

  return visited
}
