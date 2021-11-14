@file:Suppress("unused")

package com.gabrielleeg1.plank.shared

/**
 * Either datatype
 *
 * Note: not using arrow-kt for monadic error handling
 * because the project could turn into a mpp one
 */
sealed class Either<out A, out B> {
  companion object {
    fun <B> pure(value: B): Either<*, B> = Right(value)

    inline fun <reified A : Throwable, B> fromException(function: () -> B) = try {
      Right(function())
    } catch (a: Throwable) {
      if (a is A) {
        Left(a)
      } else {
        throw a
      }
    }
  }
}

data class Left<A>(val a: A) : Either<A, Nothing>()

data class Right<B>(val b: B) : Either<Nothing, B>()

fun <A, B> Either<A, Either<A, B>>.flatten(): Either<A, B> {
  if (this is Left) return this

  return (this as Right).b
}

/**
 * Returns null if [this] is [Right] and if [this]
 * is [Left] return [Left.a]
 *
 * @return [A?]
 */
fun <A, B> Either<A, B>.left(): A? = when (this) {
  is Left -> a
  is Right -> null
}

/**
 * Returns null if [this] is [Left] and if [this]
 * is [Right] return [Right.b]
 *
 * @return [A?]
 */
fun <A, B> Either<A, B>.right(): B? = when (this) {
  is Left -> null
  is Right -> b
}

/**
 * If [this] is [Left] will throw
 * an [IllegalStateException] with
 * message [message]
 *
 * @throws [IllegalStateException]
 * @return [B]
 */
fun <A, B> Either<A, B>.expect(message: String): B = when (this) {
  is Left -> throw IllegalStateException(message)
  is Right -> b
}

/**
 * If [this] is [Left] will throw
 * an [IllegalStateException] with
 * message [message]
 *
 * @throws [IllegalStateException]
 * @return [B]
 */
fun <A, B> Either<A, B>.expect(message: (A) -> String): B = when (this) {
  is Left -> throw IllegalStateException(message(a))
  is Right -> b
}

/**
 * If [this] is [Left] will throw [A]
 * and if not will return [B]
 *
 * @return [B]
 */
fun <A : Throwable, B> Either<A, B>.unwrap(): B = when (this) {
  is Left -> throw a
  is Right -> b
}

/**
 * Transforms [B] into [R] with [map]
 *
 * @return [Either]
 */
fun <A, B, R> Either<A, B>.map(map: (B) -> R): Either<A, R> = when (this) {
  is Left -> this
  is Right -> Right(map(b))
}

/**
 * Transforms [B] into [R] with [fmap]
 *
 * @return [Either]
 */
fun <A, B, R> Either<A, B>.flatMap(fmap: (B) -> Either<A, R>): Either<A, R> = when (this) {
  is Left -> this
  is Right -> fmap(b)
}

/**
 * Transforms [A] into [R] with [fmap]
 *
 * @return [Either]
 */
fun <A, B, R> Either<A, B>.mapLeft(fmap: (A) -> R): Either<R, B> = when (this) {
  is Left -> Left(fmap(a))
  is Right -> this
}

fun <A, B, R> Either<A, B>.fold(foldLeft: (A) -> R, foldRight: (B) -> R): R = when (this) {
  is Left -> foldLeft(a)
  is Right -> foldRight(b)
}
