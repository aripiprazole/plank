@file:Suppress("UNCHECKED_CAST")

package com.lorenzoog.plank.shared

interface BindScope<A> {
  fun <B> Either<out A, out B>.bind(): B

  operator fun <B> Either<out A, out B>.not(): B = bind()
}

@PublishedApi
internal class BindException(val left: Left<*>) : Exception()

inline fun <A, B> either(builder: BindScope<A>.() -> Either<out A, out B>): Either<A, B> {
  return try {
    val scope = object : BindScope<A> {
      override fun <B> Either<out A, out B>.bind(): B {
        return when (this) {
          is Left -> throw BindException(this)
          is Right -> b
        }
      }
    }

    scope.builder() as Either<A, B>
  } catch (exception: BindException) {
    exception.left as Either<A, B>
  }
}
