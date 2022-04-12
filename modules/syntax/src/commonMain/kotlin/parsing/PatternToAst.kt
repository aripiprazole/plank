package org.plank.syntax.parsing

import org.plank.parser.PlankParser.IdentPatternContext
import org.plank.parser.PlankParser.NamedTuplePatternContext
import org.plank.parser.PlankParser.PatternContext
import org.plank.syntax.element.EnumVariantPattern
import org.plank.syntax.element.IdentPattern
import org.plank.syntax.element.Pattern
import org.plank.syntax.element.PlankFile

fun PatternContext.patternToAst(file: PlankFile): Pattern = when (this) {
  is IdentPatternContext -> IdentPattern(name!!.tokenToAst(file), treeLoc(file))
  is NamedTuplePatternContext -> EnumVariantPattern(
    type = type!!.pathToAst(file),
    properties = findPattern().map { it.patternToAst(file) },
    loc = treeLoc(file),
  )
  else -> error("Unsupported pattern ${this::class.simpleName}")
}
