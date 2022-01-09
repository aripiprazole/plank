package com.gabrielleeg1.plank.grammar.debug

import com.gabrielleeg1.plank.grammar.element.PlankElement

fun PlankElement.printTree(): String {
  return printTree(asMap())
}

fun printTree(
  expr: Map<String, Any?> = mapOf(),
  prefix: String = "",
  childrenPrefix: String = "",
  name: Any = "Element",
): String = buildString {
  append(prefix)
  append(name)
  append('\n')

  fun printTree(name: String = "", value: Any?): String {
    return when (value) {
      is List<*> -> {
        printTree(value.asMap(), "$childrenPrefix├── $name: ", "$childrenPrefix│   ", "List<Expr>")
      }
      is PlankElement -> {
        val typeName = value::class.simpleName!!

        printTree(value.asMap(), "$childrenPrefix├── $name: ", "$childrenPrefix│   ", typeName)
      }
      else -> {
        printTree(mapOf(), "$childrenPrefix├── $name: ", "$childrenPrefix│   ", value.toString())
      }
    }
  }

  return toString() + expr.entries.joinToString(separator = "") { (name, value) ->
    printTree(name, value)
  }
}
