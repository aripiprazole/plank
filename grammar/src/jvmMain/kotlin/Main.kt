package com.gabrielleeg1.plank.grammar

import com.gabrielleeg1.plank.grammar.debug.printTree
import com.gabrielleeg1.plank.grammar.element.PlankFile

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

  println("Dumped plank file: ${file.printTree()}")
}
