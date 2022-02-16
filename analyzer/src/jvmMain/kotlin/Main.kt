package org.plank.analyzer

import org.plank.syntax.debug.dumpTree
import org.plank.syntax.element.PlankFile
import org.plank.syntax.message.SimpleCompilerLogger

fun main() {
  val file = PlankFile.of(
    """
    module Main;

    @intrinsic
    fun println(message: *Char) -> ();

    fun main(argc: Int32, argv: **Char) {
      fun nested() {
        println("Hello, world! (nested)");
      }

      println("Hello, world!");
    }
    """.trimIndent(),
  )

  val resolved = analyze(file)
  val logger = SimpleCompilerLogger(debug = true, verbose = true)

  resolved.bindingViolations.forEach { it.render(logger) }

//  println(file.dumpTree())
  println(resolved.dumpTree())
}
