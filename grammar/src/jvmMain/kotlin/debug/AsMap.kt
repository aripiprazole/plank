package com.lorenzoog.jplank.debug

import kotlin.reflect.full.memberProperties

internal actual fun List<*>.asMap(): Map<String, Any?> {
  return withIndex().associate { (index, value) ->
    index.toString() to value
  }
}

internal actual fun Any?.asMap(): Map<String, Any?> {
  if (this == null) return mapOf()

  return this::class.memberProperties.associate { property ->
    property.name to property.call(this)
  }
}
