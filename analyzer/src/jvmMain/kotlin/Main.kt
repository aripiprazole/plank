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

    enum List {
      Cons(*Char, List),
      Nil
    }

    fun print_list(list: List) {
      match list {
        Cons(value, next) => println("cons"),
        Nil() => println("nil")
      };
    }

    fun main(argc: Int32, argv: **Char) -> () {
      print_list(Cons("hello", Nil));
      print_list(Nil);
    }
    """.trimIndent()
  )

  val resolved = analyze(file)
  val logger = SimpleCompilerLogger(debug = true, verbose = true)

  resolved.bindingViolations.forEach { it.render(logger) }

  println(resolved.dumpTree())
}
