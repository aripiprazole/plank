package org.plank.analyzer

import org.plank.syntax.debug.dumpTree
import org.plank.syntax.element.PlankFile
import org.plank.syntax.message.SimpleCompilerLogger

fun main() {
  val file = PlankFile.of(
    """
    module Main;

    type Person[a] = {
      name: a,
      age: Int32
    }

    fun show_person(person: Person[Int32]) {}

    fun main(argc: Int32, argv: **Char) {
      let person = Person{name: "John", age: 42};
      show_person(person);
    }
    """.trimIndent()
  )

  val resolved = analyze(file)
  val logger = SimpleCompilerLogger(debug = true, verbose = true)

  resolved.analyzerViolations.forEach { it.render(logger) }

  println(file.dumpTree())
  println(resolved.dumpTree())
}
