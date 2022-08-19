package org.plank.typing

class Hole<T : Type>(private var value: T) {
  fun unwrap(): T = value

  fun overwrite(hole: Hole<T>) {
    this.value = hole.value
  }

  inline fun <reified A : T> unwrapAs(): A = unwrap() as A

  inline fun <reified A : T> instanceOf(): Boolean = unwrap() is A

  override fun hashCode(): Int = super.hashCode()

  override fun equals(other: Any?): Boolean {
    if (other !is Hole<*>) return false

    return value == other.value
  }

  override fun toString(): String = holeToString(this)
}

expect fun <T : Type> holeIdentity(hole: Hole<T>): String

expect fun <T : Type> holeToString(hole: Hole<T>): String

fun <T : Type> T.asHole(): Hole<T> = Hole(this)
