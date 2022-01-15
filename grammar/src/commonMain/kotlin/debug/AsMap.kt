package com.gabrielleeg1.plank.grammar.debug

// TODO: use kotlinx serialization to work with all targets

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class DontDump

internal expect fun List<*>.asMap(): Map<String, Any?>

internal expect fun Any?.asMap(): Map<String, Any?>
