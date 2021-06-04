package com.lorenzoog.plank.grammar.element

import com.lorenzoog.plank.grammar.generated.PlankLexer
import com.lorenzoog.plank.grammar.generated.PlankParser
import com.lorenzoog.plank.grammar.mapper.DescriptorMapper
import com.lorenzoog.plank.grammar.mapper.SyntaxErrorListener
import com.lorenzoog.plank.grammar.mapper.SyntaxViolation
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import pw.binom.io.file.File
import pw.binom.io.file.name
import pw.binom.io.file.nameWithoutExtension
import pw.binom.io.file.read
import pw.binom.io.readText
import pw.binom.io.utf8Reader

data class PlankFile(
  val content: String,
  val moduleName: String? = null,
  val path: String = "Anonymous",
  val program: List<Decl> = emptyList(),
  val violations: List<SyntaxViolation> = emptyList(),
) : PlankElement {
  interface Visitor<T> {
    fun visit(file: PlankFile): T = visitPlankFile(file)

    fun visitPlankFile(file: PlankFile): T
  }

  val realFile = File(path)
  val module: String = moduleName ?: realFile.name
  val isValid get() = violations.isEmpty()

  override val location = Location.undefined()

  companion object {
    fun of(file: File): PlankFile {
      return of(file.read().utf8Reader().readText(), file.nameWithoutExtension, file.path)
        .copy(path = file.path)
        .let {
          if (it.moduleName == null) {
            it.copy(moduleName = file.nameWithoutExtension)
          } else {
            it
          }
        }
    }

    fun of(text: String, module: String = "anonymous", path: String = module): PlankFile {
      val file = PlankFile(text, moduleName = module, path = path)
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
