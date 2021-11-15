package com.gabrielleeg1.plank.grammar.element

import com.gabrielleeg1.plank.grammar.generated.PlankLexer
import com.gabrielleeg1.plank.grammar.generated.PlankParser
import com.gabrielleeg1.plank.grammar.mapper.DescriptorMapper
import com.gabrielleeg1.plank.grammar.mapper.SyntaxViolation
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
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
  val module = moduleName?.toIdentifier() ?: Identifier.of(realFile.name)
  val isValid get() = violations.isEmpty()

  override val location = Location.undefined()

  companion object {
    fun parser(code: String): PlankParser {
      TODO()
    }

    fun of(file: File): PlankFile {
      return of(file.readText(), file.nameWithoutExtension, file.path)
        .copy(path = file.path)
        .let {
          if (it.moduleName == null) {
            it.copy(moduleName = QualifiedPath.from(file.nameWithoutExtension))
          } else {
            it
          }
        }
    }

    fun of(text: String, module: String = "anonymous", path: String = module): PlankFile {
      val file = PlankFile(text, moduleName = QualifiedPath.from(module), path = path)
      val stream = CharStreams.fromString(text)
      val lexer = PlankLexer(stream)
      val parser = PlankParser(CommonTokenStream(lexer))

      return DescriptorMapper(file).visitPlankFile(parser.plankFile())
    }
  }
}
