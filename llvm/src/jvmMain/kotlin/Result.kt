package com.lorenzoog.jplank.compiler.llvm

import org.llvm4j.optional.Err
import org.llvm4j.optional.Ok
import org.llvm4j.optional.Result

fun <A, B> Result<A, B>.orNull(): A? {
  return when(this) {
    is Ok -> value
    is Err -> null
  }
}
