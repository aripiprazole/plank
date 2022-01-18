package com.gabrielleeg1.plank.grammar.element

import com.gabrielleeg1.plank.grammar.debug.DontDump
import com.gabrielleeg1.plank.grammar.mapper.DescriptorMapper
import com.gabrielleeg1.plank.grammar.mapper.SyntaxErrorListener
import com.gabrielleeg1.plank.grammar.mapper.SyntaxViolation
import com.gabrielleeg1.plank.grammar.message.CompilerLogger
import com.gabrielleeg1.plank.grammar.message.SimpleCompilerLogger
import com.gabrielleeg1.plank.grammar.parser.toParseTree
import com.gabrielleeg1.plank.parser.PlankLexer
import com.gabrielleeg1.plank.parser.PlankParser
import com.strumenta.kotlinmultiplatform.BitSet
import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.Parser
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer
import org.antlr.v4.kotlinruntime.atn.ATNConfigSet
import org.antlr.v4.kotlinruntime.atn.PredictionMode
import org.antlr.v4.kotlinruntime.dfa.DFA
import pw.binom.io.file.File
import pw.binom.io.file.name
import pw.binom.io.file.nameWithoutExtension
import pw.binom.io.file.readText

data class PlankFile(
  @DontDump
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
    fun parser(text: String): PlankParser {
      val stream = CharStreams.fromString(text)
      val lexer = PlankLexer(stream)
      val parser = PlankParser(CommonTokenStream(lexer))

      parser.interpreter?.apply {
        predictionMode = PredictionMode.SLL
      }

      return parser
    }

    fun of(
      file: File,
      treeDebug: Boolean = false,
      parserDebug: Boolean = false,
      logger: CompilerLogger = SimpleCompilerLogger(),
    ): PlankFile {
      val module = file.nameWithoutExtension
      val path = file.path

      return of(file.readText(), module, path, treeDebug, parserDebug, logger)
        .copy(path = file.path)
        .let {
          if (it.moduleName == null) {
            it.copy(moduleName = QualifiedPath(file.nameWithoutExtension))
          } else {
            it
          }
        }
    }

    fun of(
      text: String,
      module: String = "anonymous",
      path: String = module,
      treeDebug: Boolean = false,
      parserDebug: Boolean = false,
      logger: CompilerLogger = SimpleCompilerLogger(),
    ): PlankFile {
      val file = PlankFile(text, moduleName = QualifiedPath(module), path = path)

      val syntaxErrorListener = SyntaxErrorListener(file)
      val parser = parser(text).apply {
        addErrorListener(syntaxErrorListener)
        if (parserDebug) {
          addErrorListener(PlankErrorListener(logger))
        }
      }

      return DescriptorMapper(file)
        .visitFile(parser.file().also { tree ->
          if (treeDebug) {
            logger.debug("Parse tree:")
            logger.debug(tree.toParseTree().multilineString())
            logger.debug()
          }
        })
        .copy(violations = syntaxErrorListener.violations)
    }
  }
}

class PlankErrorListener(private val logger: CompilerLogger) : BaseErrorListener() {
  override fun reportAmbiguity(
    recognizer: Parser,
    dfa: DFA,
    startIndex: Int,
    stopIndex: Int,
    exact: Boolean,
    ambigAlts: BitSet,
    configs: ATNConfigSet
  ) {
    logger.severe("===Ambiguity===")
    logger.severe("  data = {recognizer: Parser, dfa: DFA, startIndex: $startIndex, stopIndex: $stopIndex, exact: $exact, ambigAlts: $ambigAlts, configs: $configs}")
    logger.severe("=============================")
    logger.severe()
  }

  override fun reportAttemptingFullContext(
    recognizer: Parser,
    dfa: DFA,
    startIndex: Int,
    stopIndex: Int,
    conflictingAlts: BitSet,
    configs: ATNConfigSet
  ) {
    logger.severe("===Attempting Full Context===")
    logger.severe(" data = {recognizer: Parser, dfa: DFA, startIndex: $startIndex, stopIndex: $stopIndex, conflictingAlts: $conflictingAlts, configs: $configs}")
    logger.severe("=============================")
    logger.severe()
  }

  override fun reportContextSensitivity(
    recognizer: Parser,
    dfa: DFA,
    startIndex: Int,
    stopIndex: Int,
    prediction: Int,
    configs: ATNConfigSet
  ) {
    logger.severe("===Context Sensitivity===")
    logger.severe("  data = {recognizer: Parser, dfa: DFA, startIndex: $startIndex, stopIndex: $stopIndex, prediction: $prediction, configs: $configs}")
    logger.severe("=============================")
    logger.severe()
  }

  override fun syntaxError(
    recognizer: Recognizer<*, *>,
    offendingSymbol: Any?,
    line: Int,
    charPositionInLine: Int,
    msg: String,
    e: RecognitionException?
  ) {
    logger.severe("===Syntax Error===")
    logger.severe(" message = $msg at $offendingSymbol")
    logger.severe("==================")
    logger.severe()
  }

}
