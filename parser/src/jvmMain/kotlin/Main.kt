package org.plank.parser

import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.ConsoleErrorListener
import org.antlr.v4.kotlinruntime.DiagnosticErrorListener
import org.antlr.v4.kotlinruntime.atn.PredictionMode
import org.plank.syntax.parser.toParseTree

fun main() {
  val stream = CharStreams.fromString(
    """
    module Main;

    import Std.IO;

    type Person = {mutable name: *Char};

    fun create_gabrielle(): *Person {
      return &Person{name: "Gabrielle"};
    }

    fun create_alfredo(): *Person {
      return &Person{name: "Alfredo"};
    }

    fun create_gerson(): Person {
      return Person{name: "Gerson"};
    }

    fun main(argc: Int32, argv: **Char): Void {
      println(create_gerson().name);
      let mutable person = *create_gabrielle();
      println(person.name);
      person := *create_alfredo();
      println(person.name);
      person.name := "Alberto";
      println(person.name);
    }
    """.trimIndent()
  )
  val lexer = PlankLexer(stream)
  val parser = PlankParser(CommonTokenStream(lexer))

  parser.interpreter?.apply {
    predictionMode = PredictionMode.SLL
  }

  parser.addErrorListener(ConsoleErrorListener.INSTANCE)
  parser.addErrorListener(DiagnosticErrorListener())

  println(parser.file().toParseTree().multilineString())
}
