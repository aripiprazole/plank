package org.plank.grammar.element

import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.DiagnosticErrorListener
import org.antlr.v4.kotlinruntime.atn.PredictionMode
import org.plank.grammar.debug.DontDump
import org.plank.grammar.mapper.DescriptorMapper
import org.plank.grammar.mapper.SyntaxErrorListener
import org.plank.grammar.mapper.SyntaxViolation
import org.plank.grammar.message.CompilerLogger
import org.plank.grammar.message.SimpleCompilerLogger
import org.plank.grammar.parser.toParseTree
import org.plank.parser.PlankLexer
import org.plank.parser.PlankParser
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
          addErrorListener(DiagnosticErrorListener())
        }
      }

      return runCatching {
        DescriptorMapper(file)
          .visitFile(
            parser.file().also { tree ->
              if (treeDebug) {
                logger.debug("Parse tree:")
                logger.debug(tree.toParseTree().multilineString())
                logger.debug()
              }
            }
          )
          .copy(violations = syntaxErrorListener.violations)
      }.getOrElse {
        file.copy(violations = syntaxErrorListener.violations)
      }
    }
  }
}
