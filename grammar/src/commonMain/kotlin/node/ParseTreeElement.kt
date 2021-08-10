package com.lorenzoog.plank.grammar.node

import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Identifier
import com.lorenzoog.plank.grammar.element.Pattern
import com.lorenzoog.plank.grammar.element.QualifiedPath
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.grammar.element.TypeRef

sealed class ParseTreeElement {
  abstract fun toStringTree(ident: String = ""): String
}

class ParseTreeLeaf(val text: String) : ParseTreeElement() {
  override fun toStringTree(ident: String): String {
    return ident + "T[$text]\n"
  }

  override fun toString(): String {
    return "T[$text]"
  }
}

class ParseTreeError(val error: String) : ParseTreeElement() {
  override fun toStringTree(ident: String): String {
    return ident + "E[$error]\n"
  }

  override fun toString(): String {
    return "E[$error]"
  }
}

class ParseTreeNode(val name: String) : ParseTreeElement() {
  constructor(name: String, builder: ParseTreeNode.() -> Unit) : this(name) {
    builder()
  }

  val children = linkedSetOf<ParseTreeElement>()

  fun child(vararg newChildren: ParseTreeElement) {
    newChildren.forEach(children::add)
  }

  override fun toStringTree(ident: String): String = buildString {
    append(ident).append(name).append('\n')

    children.forEach { child ->
      append(child.toStringTree("$ident  "))
    }
  }

  override fun toString(): String {
    return "Node($name) $children"
  }
}

fun Expr.toParseTreeElement(): ParseTreeElement {
  return accept(AstParseTreeElementVisitor)
}

fun Stmt.toParseTreeElement(): ParseTreeElement {
  return accept(AstParseTreeElementVisitor)
}

fun Decl.toParseTreeElement(): ParseTreeElement {
  return accept(AstParseTreeElementVisitor)
}

fun TypeRef.toParseTreeElement(): ParseTreeElement {
  return accept(AstParseTreeElementVisitor)
}

fun Identifier.toParseTreeElement(): ParseTreeElement {
  return accept(AstParseTreeElementVisitor)
}

fun Pattern.toParseTreeElement(): ParseTreeElement {
  return accept(AstParseTreeElementVisitor)
}

fun QualifiedPath.toParseTreeElement(): ParseTreeElement {
  return accept(AstParseTreeElementVisitor)
}
