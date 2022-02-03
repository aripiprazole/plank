package org.plank.grammar

import org.plank.grammar.debug.dumpTree
import org.plank.grammar.element.PlankFile

fun main() {
  println("Parsing plank file...")

  val file = PlankFile.of(
    """
    fun main(argc: Int, argv: **Char): Void {
      println("Hello, world");
    }
    """.trimIndent(),
  )

  println("Parsed plank file: $file")

  println("Dumped plank file: ${file.dumpTree()}")
}
