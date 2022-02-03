package org.plank.grammar.debug

import kotlin.reflect.KType

// TODO: use kotlinx serialization to work with all targets

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class DontDump

class DumpEntry(val type: KType, val value: Any?) {
  override fun toString(): String = value.toString()
}

internal expect fun List<*>.asMap(): Map<String, DumpEntry>

internal expect fun Any?.asMap(): Map<String, DumpEntry>
