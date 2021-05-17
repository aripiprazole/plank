package com.lorenzoog.plank.grammar.debug

internal expect fun List<*>.asMap(): Map<String, Any?>

internal expect fun Any?.asMap(): Map<String, Any?>
