package org.plank.shared

fun <A> LinkedHashSet<A>.pop(): A {
  val value = elementAt(this.size - 1)
  remove(value)
  return value
}
