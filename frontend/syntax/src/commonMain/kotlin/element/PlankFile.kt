package org.plank.syntax.element

import okio.Path
import okio.Path.Companion.toPath
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.DiagnosticErrorListener
import org.antlr.v4.kotlinruntime.atn.PredictionMode
import org.plank.shared.nameWithoutExtension
import org.plank.shared.readText
import org.plank.syntax.SyntaxErrorListener
import org.plank.syntax.SyntaxViolation
import org.plank.syntax.debug.DontDump
import org.plank.syntax.message.CompilerLogger
import org.plank.syntax.parser.PlankLexer
import org.plank.syntax.parser.PlankParser
import org.plank.syntax.parser.toParseTree
import org.plank.syntax.parsing.fileToAst

data class PlankFile(
  @DontDump
  val content: String,
  val moduleName: QualifiedPath? = null,
  val path: String = "Anonymous",
  val program: List<Decl> = emptyList(),
  val violations: List<SyntaxViolation> = emptyList(),
) : SimplePlankElement {
  val realFile = path.toPath()
  val module = moduleName?.toIdentifier() ?: Identifier(realFile.name)
  val isValid get() = violations.isEmpty()

  override val loc: Loc = GeneratedLoc

  companion object {
    private fun parser(text: String): PlankParser {
      val stream = CharStreams.fromString(text)
      val lexer = PlankLexer(stream)
      val parser = PlankParser(CommonTokenStream(lexer))

      parser.interpreter?.apply {
        predictionMode = PredictionMode.SLL
      }

      return parser
    }

    fun of(
      file: Path,
      treeDebug: Boolean = false,
      parserDebug: Boolean = false,
      logger: CompilerLogger = CompilerLogger(),
    ): PlankFile {
      val module = file.nameWithoutExtension
      val path = file.toString()

      return of(file.readText(), module, path, treeDebug, parserDebug, logger)
        .copy(path = path)
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
      logger: CompilerLogger = CompilerLogger(),
    ): PlankFile {
      val file = PlankFile(text, moduleName = QualifiedPath(module), path = path)

      val syntaxErrorListener = SyntaxErrorListener(file)
      val parser = parser(text).apply {
        addErrorListener(syntaxErrorListener)
        if (parserDebug) {
          addErrorListener(DiagnosticErrorListener())
        }
      }

      return parser.file()
        .also { tree ->
          if (treeDebug) {
            logger.debug("Parse tree:")
            logger.debug(tree.toParseTree().multilineString())
            logger.debug()
          }
        }
        .fileToAst(file)
        .copy(violations = syntaxErrorListener.violations)
    }
  }

  override fun toString(): String =
    "PlankFile(module=$module, moduleName=$moduleName, path=$path, violations=$violations)"
}
