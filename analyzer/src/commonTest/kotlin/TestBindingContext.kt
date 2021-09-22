package com.lorenzoog.plank.analyzer.test

import com.lorenzoog.plank.analyzer.BindingContext
import com.lorenzoog.plank.analyzer.ModuleTree
import com.lorenzoog.plank.grammar.element.PlankFile
import com.lorenzoog.plank.grammar.parser.LanguageParser

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
