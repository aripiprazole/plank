package org.plank.analyzer

import org.plank.syntax.element.PlankFile
import org.plank.syntax.message.SimpleCompilerLogger

fun main() {
  val file = PlankFile.of(
    """
    module Main;

    enum List[a] {
      Cons(a, a, List[a]),
      Nil
    }

    enum Maybe[a] {
      Just(a),
      Nothing
    }

    @intrinsic
    fun println(value: *Char);

    @intrinsic
    fun ty(value: a) -> *Char;

    fun fst(list: List[a]) -> Maybe[a] = match list {
      Cons(x, _, _) => Just(x)
      Nil() => Nothing
    };

    type Person = {mutable name: *Char, age: Int32};

    module P {
      let a = 32;
    }

    fun main(argc: Int32, argv: **Char) {
      let b = if true { println("Hello"); } else println("World");
      let a = if true then true else false;
      let block: *Char = {
        println("Hello, world");
        println("Batata");
        "nao"
      };
      let mutable person = Person { name: "hello", age: 32 };
      person.name := "world";
      person := Person { name: "new", age: 32 };
      println(person.name);
      fst(Cons("Hello", " world", Nil));
    }
    """.trimIndent()
  )
  val resolved = analyze(file)
  val logger = SimpleCompilerLogger(debug = true, verbose = true)

  resolved.analyzerViolations.forEach { it.render(logger) }

  println(resolved.pretty())
}
