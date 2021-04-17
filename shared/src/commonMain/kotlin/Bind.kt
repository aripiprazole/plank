@file:Suppress("UNCHECKED_CAST")

package com.lorenzoog.plank.shared

class BindScope<A> @PublishedApi internal constructor() {
  fun <B> Either<A, B>.bind(): B {
    return when (this) {
      is Left -> throw BindException(this)
      is Right -> b
    }
  }

  operator fun <B> Either<A, B>.not(): B = bind()
}

@PublishedApi
internal class BindException(val left: Left<*>) : Exception()

inline fun <A, B> either(builder: BindScope<A>.() -> Either<A, B>): Either<A, B> {
  return try {
    BindScope<A>().builder()
  } catch (exception: BindException) {
    exception.left as Either<A, B>
  }
}
