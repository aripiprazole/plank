package com.gabrielleeg1.plank.grammar.debug

import com.gabrielleeg1.plank.grammar.element.PlankElement

fun PlankElement.dumpTree(): String {
  return dumpTree(asMap())
}

fun dumpTree(
  expr: Map<String, Any?> = mapOf(),
  prefix: String = "",
  childrenPrefix: String = "",
  name: Any = "Element",
): String = buildString {
  append(prefix)
  append(name)
  append('\n')

  fun dumpTree(name: String = "", value: Any?): String {
    return when (value) {
      is List<*> -> {
        dumpTree(value.asMap(), "$childrenPrefix├── $name: ", "$childrenPrefix│   ", "List<Expr>")
      }
      is PlankElement -> {
        val typeName = value::class.simpleName!!

        dumpTree(value.asMap(), "$childrenPrefix├── $name: ", "$childrenPrefix│   ", typeName)
      }
      else -> {
        dumpTree(mapOf(), "$childrenPrefix├── $name: ", "$childrenPrefix│   ", value.toString())
      }
    }
  }

  return toString() + expr.entries.joinToString(separator = "") { (name, value) ->
    dumpTree(name, value)
  }
}
