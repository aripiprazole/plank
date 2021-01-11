package com.lorenzoog.jplank.debug

import com.lorenzoog.jplank.element.PkElement

object TreePrinter {
  fun print(any: Any): String {
    return print(any.asMap())
  }

  private fun print(
    expr: Map<String, Any?> = mapOf(),
    prefix: String = "",
    childrenPrefix: String = "",
    name: Any = "Expr",
  ): String = buildString {
    append(prefix)
    append(name)
    append('\n')

    fun print(name: String = "", value: Any?): String {
      return when (value) {
        is List<*> -> {
          print(value.asMap(), "$childrenPrefix├── $name: ", "$childrenPrefix│   ", "List<Expr>")
        }
        is PkElement -> {
          val typeName = value::class.simpleName!!

          print(value.asMap(), "$childrenPrefix├── $name: ", "$childrenPrefix│   ", typeName)
        }
        else -> {
          print(mapOf(), "$childrenPrefix├── $name: ", "$childrenPrefix│   ", value.toString())
        }
      }
    }

    return toString() + expr.entries.joinToString(separator = "") { (name, value) ->
      print(name, value)
    }
  }
}
