package org.plank.analyzer

import org.plank.syntax.debug.dumpTree
import org.plank.syntax.element.PlankFile
import org.plank.syntax.message.SimpleCompilerLogger

fun main() {
  val file = PlankFile.of(
    """
    module Main;

    enum Maybe[a] {
      Just(a),
      Nothing
    }

    fun use_maybe(m: Maybe[a]) {}

    fun main(argc: Int32, argv: **Char) {
      use_maybe(match Just(10) {
        Just(x) => Just(x),
        Nothing => Just(0)
      });
    }
    """.trimIndent()
  )

  val resolved = analyze(file)
  val logger = SimpleCompilerLogger(debug = true, verbose = true)

  resolved.analyzerViolations.forEach { it.render(logger) }

  println(resolved.dumpTree())
}
