package org.plank.shared

expect object Environment {
  operator fun get(name: String): String?
}
