package com.gabrielleeg1.plank.grammar.element

import com.gabrielleeg1.plank.grammar.generated.PlankLexer
import com.gabrielleeg1.plank.grammar.generated.PlankParser
import com.gabrielleeg1.plank.grammar.mapper.DescriptorMapper
import com.gabrielleeg1.plank.grammar.mapper.SyntaxErrorListener
import com.gabrielleeg1.plank.grammar.mapper.SyntaxViolation
import com.strumenta.kotlinmultiplatform.BitSet
import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.Parser
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer
import org.antlr.v4.kotlinruntime.atn.ATNConfigSet
import org.antlr.v4.kotlinruntime.dfa.DFA
import pw.binom.io.file.File
import pw.binom.io.file.name
import pw.binom.io.file.nameWithoutExtension
import pw.binom.io.file.readText

data class PlankFile(
  val content: String,
  val moduleName: QualifiedPath? = null,
  val path: String = "Anonymous",
  val program: List<Decl> = emptyList(),
  val violations: List<SyntaxViolation> = emptyList(),
) : PlankElement {
  interface Visitor<T> {
    fun visit(file: PlankFile): T = visitPlankFile(file)

    fun visitPlankFile(file: PlankFile): T
  }

  val realFile = File(path)
  val module = moduleName?.toIdentifier() ?: Identifier(realFile.name)
  val isValid get() = violations.isEmpty()

  override val location = Location.Generated

  companion object {
    fun of(file: File): PlankFile {
      return of(file.readText(), file.nameWithoutExtension, file.path)
        .copy(path = file.path)
        .let {
          if (it.moduleName == null) {
            it.copy(moduleName = QualifiedPath(file.nameWithoutExtension))
          } else {
            it
          }
        }
    }

    fun of(text: String, module: String = "anonymous", path: String = module): PlankFile {
      val file = PlankFile(text, moduleName = QualifiedPath(module), path = path)
      val stream = CharStreams.fromString(text)
      val lexer = PlankLexer(stream)
      val parser = PlankParser(CommonTokenStream(lexer)).apply {
        addErrorListener(SyntaxErrorListener(file))
//        addErrorListener(PlankErrorListener)
      }

      return DescriptorMapper(file).visitFile(parser.file())
    }
  }
}

@Suppress("unused")
object PlankErrorListener : BaseErrorListener() {
  override fun reportAmbiguity(
    recognizer: Parser,
    dfa: DFA,
    startIndex: Int,
    stopIndex: Int,
    exact: Boolean,
    ambigAlts: BitSet,
    configs: ATNConfigSet
  ) {
    println("ambiguity----------------------------------------------")
    println("  data = {recognizer: Parser, dfa: DFA, startIndex: $startIndex, stopIndex: $stopIndex, exact: $exact, ambigAlts: $ambigAlts, configs: $configs}")
    println("-------------------------------------------------------")
    println()
  }

  override fun reportAttemptingFullContext(
    recognizer: Parser,
    dfa: DFA,
    startIndex: Int,
    stopIndex: Int,
    conflictingAlts: BitSet,
    configs: ATNConfigSet
  ) {
    println("contextAttemptingFullContext---------------------------")
    println(" data = {recognizer: Parser, dfa: DFA, startIndex: $startIndex, stopIndex: $stopIndex, conflictingAlts: $conflictingAlts, configs: $configs}")
    println("-------------------------------------------------------")
    println()
  }

  override fun reportContextSensitivity(
    recognizer: Parser,
    dfa: DFA,
    startIndex: Int,
    stopIndex: Int,
    prediction: Int,
    configs: ATNConfigSet
  ) {
    println("contextSensitivity-------------------------------------")
    println("  data = {recognizer: Parser, dfa: DFA, startIndex: $startIndex, stopIndex: $stopIndex, prediction: $prediction, configs: $configs}")
    println("-------------------------------------------------------")
    println()
  }

  override fun syntaxError(
    recognizer: Recognizer<*, *>,
    offendingSymbol: Any?,
    line: Int,
    charPositionInLine: Int,
    msg: String,
    e: RecognitionException?
  ) {
    println("syntaxError--------------------------------------------")
    println("  data = {recognizer: Recognizer<*, *>, offendingSymbol: $offendingSymbol, line: $line, charPositionInLine: $charPositionInLine, msg: $msg, e: $e}")
    println("-------------------------------------------------------")
    println()
  }

}
