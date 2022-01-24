package com.gabrielleeg1.plank.compiler.builder

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Ior
import arrow.core.None
import arrow.core.Some
import org.llvm4j.optional.Err
import org.llvm4j.optional.Ok
import org.llvm4j.optional.Result
import arrow.core.Ior.Left as IorLeft
import arrow.core.Ior.Right as IorRight
import org.llvm4j.optional.None as LlvmNone
import org.llvm4j.optional.Option as LlvmOption
import org.llvm4j.optional.Some as LlvmSome

fun <A, B> Result<B, A>.arrow(): Either<A, B> = when (this) {
  is Ok -> Right(value)
  is Err -> Left(error)
}

fun <A, B> Result<B, A>.ior(): Ior<A, B> = when (this) {
  is Ok -> IorRight(value)
  is Err -> IorLeft(error)
}

fun <A> LlvmOption<A>.arrow(): arrow.core.Option<A> = when (this) {
  is LlvmSome -> Some(value)
  is LlvmNone -> None
}

fun <B> Either<*, B>.unwrap(): B {
  return when (this) {
    is Right -> value
    is Left -> error("Called `Either.unwrap()` on a `Left` value")
  }
}
