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
    fun panic(message: *Char) -> a;

    @intrinsic
    fun println(message: *Char) -> ();

    fun unwrap(m: Maybe[a]) -> a = match m {
      Just(x)   => x,
      Nothing() => panic("unwrap called on `Nothing`")
    }

    fun fst(list: List[a]) -> Maybe[a] = match list {
      Cons(x, _) => Just(x),
      Nil()      => Nothing
    }

    fun main(argc: Int32, argv: **Char) {
      let list = Cons("Hello", Nil);
      println(unwrap(fst(list)));
    }
    """.trimIndent()
  )
  val resolved = analyze(file)
  val logger = SimpleCompilerLogger(debug = true, verbose = true)

  resolved.analyzerViolations.forEach { it.render(logger) }

  println(resolved.pretty())
}
