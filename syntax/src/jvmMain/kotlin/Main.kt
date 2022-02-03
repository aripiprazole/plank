package org.plank.syntax

import org.plank.syntax.debug.dumpTree
import org.plank.syntax.element.PlankFile

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
