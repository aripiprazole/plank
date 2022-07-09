package org.plank.shared

actual object Environment {
  actual operator fun get(name: String): String? = System.getenv(name)
}
