package com.lorenzoog.jplank.debug

internal expect fun List<*>.asMap(): Map<String, Any?>

internal expect fun Any?.asMap(): Map<String, Any?>
