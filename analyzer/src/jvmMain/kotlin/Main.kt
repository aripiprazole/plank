package org.plank.analyzer

import org.plank.syntax.debug.dumpTree
import org.plank.syntax.element.PlankFile

fun main() {
  val file = PlankFile.of(
    """
    native fun println(message: *Char): Void

    fun main(argc: Int32, argv: **Char): Void {
      println("Hello, world");
    }
    """.trimIndent(),
  )

  val resolved = analyze(file)

  resolved.bindingViolations.forEach { println(it) }

//  println(file.dumpTree())
  println(resolved.dumpTree())
}
