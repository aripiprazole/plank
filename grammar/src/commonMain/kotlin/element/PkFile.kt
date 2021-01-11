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

data class PkFile(
  val moduleName: String = "Anonymous",
  val imports: List<ImportDirective>,
  val program: List<Decl>,
  val violations: List<SyntaxViolation> = emptyList()
) : PkElement {
  val isValid get() = violations.isEmpty()

  override val location: Location = Location(-1, -1, moduleName)

  companion object {
    fun of(file: File): PkFile {
      return of(file.read().utf8Reader().readText(), file.path)
        .copy(moduleName = file.nameWithoutExtension)
    }

    fun of(text: String, path: String = "anonymous"): PkFile {
      val stream = CharStreams.fromString(text)
      val lexer = PlankLexer(stream)
      val parser = PlankParser(CommonTokenStream(lexer))
      val listener = SyntaxErrorListener(path).also {
        parser.addErrorListener(it)
      }

      return DescriptorMapper(path, listener.violations).visitProgram(parser.program())
    }
  }
}
