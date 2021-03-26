package com.lorenzoog.jplank.analyzer

class Graph<T> {
  val values = mutableMapOf<T, MutableSet<T>>()

  val edges: Int
    get() = values.values.fold(0) { acc, list -> acc + list.size }

  fun hasEdge(vertex: T, edge: T): Boolean {
    return values[vertex].orEmpty().contains(edge)
  }

  fun hasVertex(vertex: T): Boolean {
    return values[vertex] != null
  }

  fun addVertex(value: T) {
    values[value] = linkedSetOf()
  }

  fun addEdge(src: T, dst: T, bidirectional: Boolean = false) {
    val srcList = values.getOrPut(src) { linkedSetOf() }
    val dstList = values.getOrPut(dst) { linkedSetOf() }

    srcList.add(dst)

    if (bidirectional) {
      dstList.add(src)
    }
  }

  override fun toString(): String =
    values.entries.joinToString(separator = "\n", prefix = "{", postfix = "}") { (vertex, edges) ->
      if(edges.isEmpty()) {
        "$vertex -> Empty "
      } else {
        "$vertex -> ${edges.joinToString(" ")} "
      }
    }
}
