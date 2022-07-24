package org.plank.syntax.debug

import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.typeOf

internal actual fun List<*>.asMap(): Map<String, DumpEntry> {
  return withIndex().associate { (index, value) ->
    index.toString() to DumpEntry(
      if (value != null) value::class.starProjectedType else typeOf<Any?>(),
      value,
    )
  }
}

internal actual fun Any?.asMap(): Map<String, DumpEntry> {
  if (this == null) return mapOf()

  return this::class.memberProperties
    .filterNot { it.hasAnnotation<DontDump>() }
    .associate { property ->
      runCatching {
        property.name to DumpEntry(property.getter.returnType, property.getter.call(this))
      }.onFailure {
        it.printStackTrace()
      }.getOrThrow()
    }
}
