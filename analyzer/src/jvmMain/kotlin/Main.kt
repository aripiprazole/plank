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

    fun fst(list: List[a]) -> Maybe[a] {
      let value = match list {
        Cons(x, _) => {
          fun batata() -> a = x;

          let a = x;

          Just(batata())
        },
        Nil() => Nothing
      };

      value
    }

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
