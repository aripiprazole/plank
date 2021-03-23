package com.lorenzoog.jplank.compiler.llvm

import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.PointerPointer

inline fun <reified T: Pointer> List<T>.asPointer(): PointerPointer<T> {
  return PointerPointer<T>(*toTypedArray())
}
