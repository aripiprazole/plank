package com.lorenzoog.jplank.compiler.llvm

import org.bytedeco.javacpp.Pointer
import org.llvm4j.optional.None
import org.llvm4j.optional.Option
import org.llvm4j.optional.Some

fun <T : Pointer> T?.asOption(): Option<T> {
  return if (this == null || isNull) {
    None
  } else {
    Some(this)
  }
}

fun <T> T?.asOption(): Option<T> {
  return when (this) {
    null -> None
    else -> Some(this)
  }
}

fun <T> Option<T>.orDefault(default: T): T {
  return when (this) {
    is Some -> value
    is None -> default
  }
}

