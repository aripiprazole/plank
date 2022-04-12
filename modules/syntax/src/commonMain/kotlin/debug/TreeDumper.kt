package org.plank.syntax.debug

import org.plank.syntax.element.Loc
import org.plank.syntax.element.PlankElement

fun PlankElement.dumpTree(): String {
  return dumpTree(asMap(), name = this::class.simpleName!!)
}

fun dumpInline(map: Map<String, Any?>, prefix: String = "", name: Any = "Element"): String =
  buildString {
    append(prefix)
    append(name)
    if (map.isNotEmpty()) {
      append(
        map.entries.reversed().joinToString(", ", prefix = "(", postfix = ")") { (a, b) ->
          "$a: $b"
        }
      )
    }
    append('\n')
  }

fun isComplex(map: Map<*, *>): Boolean {
  return map.values.any {
    when (it) {
      is PlankElement -> isComplex(it.asMap())
      is String, is Boolean, is Int, is Float, is Byte, is Short, is Char, is Double -> false
      is List<*> -> true
      is Loc -> false
      else -> true // has a complex element
    }
  }
}

fun dumpTree(
  map: Map<String, DumpEntry> = mapOf(),
  prefix: String = "",
  childrenPrefix: String = "",
  name: Any = "Element",
  forceComplex: Boolean = false,
): String = buildString {
  if (!isComplex(map.mapValues { (_, b) -> b.value }) && !forceComplex) {
    return dumpInline(map, prefix, name)
  }

  append(prefix)
  append(name)
  append('\n')

  fun DumpEntry.dumpTree(name: String = ""): String {
    return when (value) {
      is List<*> -> {
        val itemType = type.arguments[0].type!!
        dumpTree(
          value.asMap(),
          "$childrenPrefix├── $name: ",
          "$childrenPrefix│   ",
          "List[$itemType]",
          true
        )
      }
      is PlankElement -> {
        val typeName = value::class.simpleName!!
        val valueAsMap = value.asMap()

        if (isComplex(valueAsMap.mapValues { (_, b) -> b.value })) {
          dumpTree(valueAsMap, "$childrenPrefix├── $name: ", "$childrenPrefix│   ", typeName)
        } else {
          dumpInline(valueAsMap, "$childrenPrefix├── $name: ", typeName)
        }
      }
      is Map<*, *> -> {
        val keyType = type.arguments[0].type!!
        val valueType = type.arguments[1].type!!

        dumpTree(
          value
            .mapKeys { (a) -> a.toString() }
            .mapValues { (_, b) -> DumpEntry(valueType, b) },
          "$childrenPrefix├── $name: ",
          "$childrenPrefix│   ",
          (if (value.isEmpty()) "EmptyMap" else "Map") + "[$keyType, $valueType]"
        )
      }
      else -> {
        dumpTree(mapOf(), "$childrenPrefix├── $name: ", "$childrenPrefix│   ", value.toString())
      }
    }
  }

  return toString() + map.entries.joinToString(separator = "") { (name, value) ->
    value.dumpTree(name)
  }
}
