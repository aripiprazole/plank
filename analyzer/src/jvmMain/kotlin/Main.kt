package org.plank.analyzer

import org.plank.grammar.debug.dumpTree
import org.plank.grammar.element.PlankFile

fun main() {
  val file = PlankFile.of(
    """
    native fun println(message: *Char): Void

    fun main(argc: Int32, argv: **Char): Void {
      println("Hello, world");
    }
    """.trimIndent(),
  )

  val resolved = analyze(file, ModuleTree())

  resolved.bindingViolations.forEach { println(it) }

//  println(file.dumpTree())
  println(resolved.dumpTree())
}
