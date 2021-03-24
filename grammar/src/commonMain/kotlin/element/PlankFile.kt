package com.lorenzoog.jplank.element

import com.lorenzoog.jplank.grammar.DescriptorMapper
import com.lorenzoog.jplank.grammar.SyntaxErrorListener
import com.lorenzoog.jplank.grammar.SyntaxViolation
import com.lorenzoog.jplank.grammar.generated.PlankLexer
import com.lorenzoog.jplank.grammar.generated.PlankParser
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import pw.binom.io.file.File
import pw.binom.io.file.nameWithoutExtension
import pw.binom.io.file.read
import pw.binom.io.readText
import pw.binom.io.utf8Reader

data class PlankFile(
  val module: String = "Anonymous",
  val path: String = "Anonymous",
  val program: List<Decl> = emptyList(),
  val violations: List<SyntaxViolation> = emptyList(),
) : PlankElement {
  interface Visitor<T> {
    fun visit(file: PlankFile): T = visitPlankFile(file)

    fun visitPlankFile(file: PlankFile): T
  }

  val isValid get() = violations.isEmpty()

  override val location: Location = Location(-1, -1, this)

  companion object {
    fun of(file: File): PlankFile {
      return of(file.read().utf8Reader().readText(), file.nameWithoutExtension, file.path)
        .copy(path = file.path, module = file.nameWithoutExtension)
    }

    fun of(text: String, module: String = "anonymous", path: String = module): PlankFile {
      val file = PlankFile(module = module, path = path)
      val stream = CharStreams.fromString(text)
      val lexer = PlankLexer(stream)
      val parser = PlankParser(CommonTokenStream(lexer))
      val listener = SyntaxErrorListener(file).also {
        parser.addErrorListener(it)
      }

      return DescriptorMapper(file, listener.violations).visitProgram(parser.program())
    }
  }
}
