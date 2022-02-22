package org.plank.analyzer

import org.plank.syntax.debug.dumpTree
import org.plank.syntax.element.PlankFile
import org.plank.syntax.message.SimpleCompilerLogger

fun main() {
  val file = PlankFile.of(
    """
    module Main;


    enum List[a] {
      Cons(a, List[a]),
      Nil
    }

    @intrinsic
    fun ty(value: a) -> *Char;

    fun show_list(list: List[a]) -> *Char {
      ty(list)
    }

    fun main(argc: Int32, argv: **Char) {
      show_list(Cons("Hello", Nil));
    }
    """.trimIndent()
  )
  val resolved = analyze(file)
  val logger = SimpleCompilerLogger(debug = true, verbose = true)

  resolved.analyzerViolations.forEach { it.render(logger) }

  println(resolved.dumpTree())
}
