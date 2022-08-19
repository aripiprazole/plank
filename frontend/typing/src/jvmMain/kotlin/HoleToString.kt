package org.plank.typing

actual fun <T : Type> holeToString(hole: Hole<T>): String {
  return "Hole[${Integer.toHexString(hole.hashCode())}](${hole.unwrap()})"
}

actual fun <T : Type> holeIdentity(hole: Hole<T>): String {
  return Integer.toHexString(hole.hashCode())
}
