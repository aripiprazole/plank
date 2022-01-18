package com.gabrielleeg1.plank.grammar.parser

import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.tree.TerminalNode
import pw.binom.collection.LinkedList

sealed interface ParseTreeElement {
  override fun toString(): String

  fun multilineString(ident: String = ""): String
}

class ParseTreeLeaf(val text: String) : ParseTreeElement {
  override fun toString(): String = "T[$text]"

  override fun multilineString(ident: String): String = ident + "T[$text]\n"
}

class ParseTreeNode(val name: String): ParseTreeElement {
  val children = LinkedList<ParseTreeElement>()

  fun child(element: ParseTreeElement): ParseTreeNode {
    children.add(element)
    return this
  }

  override fun toString(): String = "Node($name) $children"

  override fun multilineString(ident: String): String = buildString {
    append(ident)
    append(name)
    append("\n")
    children.forEach { element ->
      append(element.multilineString("$ident  "))
    }
  }
}

fun ParserRuleContext.toParseTree(): ParseTreeNode {
  val tree = ParseTreeNode(this::class.simpleName ?: error("Unknown name for parse context: $this"))
  children.orEmpty().forEach { element ->
    when (element) {
      is ParserRuleContext -> tree.child(element.toParseTree())
      is TerminalNode -> tree.child(ParseTreeLeaf(element.text))
    }
  }
  return tree
}
