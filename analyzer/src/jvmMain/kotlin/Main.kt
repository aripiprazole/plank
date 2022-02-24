package org.plank.analyzer

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

    enum Maybe[a] {
      Just(a),
      Nothing
    }

    @intrinsic
    fun ty(value: a) -> *Char;

    fun fst(list: List[*Char]) -> Maybe[*Char] = match list {
      Cons(x, _) => Just(x),
      Nil() => Nothing
    };

    fun main(argc: Int32, argv: **Char) {
      fst(Cons("Hello", Nil));
    }
    """.trimIndent()
  )
  val resolved = analyze(file)
  val logger = SimpleCompilerLogger(debug = true, verbose = true)

  resolved.analyzerViolations.forEach { it.render(logger) }

  println(resolved.pretty())
}
