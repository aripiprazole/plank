package com.lorenzoog.jplank.analyzer

fun main() {
  val graph = Graph<String>().apply {
    addVertex("Std.IO")
    addVertex("Std.Control")
    addVertex("Std.Show")
    addVertex("Std.Functor")

    addVertex("Main")

    addEdge("Std.IO", "Std.Show")
    addEdge("Std.Show", "Std.Control")
    addEdge("Std.Functor", "Std.Control")
    addEdge("Main", "Std.IO")
    addEdge("Main", "Std.Show")
  }

  println(graph.depthFirstSearch("Main"))
}
