package com.gabrielleeg1.plank.analyzer.test

import com.gabrielleeg1.plank.analyzer.BindingContext
import com.gabrielleeg1.plank.analyzer.ModuleTree
import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.grammar.parser.LanguageParser

class TestBindingContext(
  code: String,
  tree: ModuleTree
) : BindingContext by BindingContext(tree), LanguageParser by LanguageParser(code)

fun <T> bindContext(
  code: String,
  dependencies: List<PlankFile> = emptyList(),
  block: TestBindingContext.() -> T
): T {
  return TestBindingContext(code, ModuleTree(dependencies)).block()
}
