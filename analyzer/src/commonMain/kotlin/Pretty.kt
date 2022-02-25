@file:Suppress("LongMethod", "ComplexMethod")

package org.plank.analyzer

import org.plank.analyzer.element.ResolvedCodeBody
import org.plank.analyzer.element.ResolvedEnumDecl
import org.plank.analyzer.element.ResolvedExprBody
import org.plank.analyzer.element.ResolvedExprStmt
import org.plank.analyzer.element.ResolvedFunDecl
import org.plank.analyzer.element.ResolvedFunctionBody
import org.plank.analyzer.element.ResolvedLetDecl
import org.plank.analyzer.element.ResolvedModuleDecl
import org.plank.analyzer.element.ResolvedNoBody
import org.plank.analyzer.element.ResolvedPlankFile
import org.plank.analyzer.element.ResolvedReturnStmt
import org.plank.analyzer.element.ResolvedStmt
import org.plank.analyzer.element.ResolvedStructDecl
import org.plank.analyzer.element.ResolvedUseDecl
import org.plank.analyzer.element.TypedAccessExpr
import org.plank.analyzer.element.TypedAssignExpr
import org.plank.analyzer.element.TypedBlockBranch
import org.plank.analyzer.element.TypedBlockExpr
import org.plank.analyzer.element.TypedCallExpr
import org.plank.analyzer.element.TypedConstExpr
import org.plank.analyzer.element.TypedDerefExpr
import org.plank.analyzer.element.TypedExpr
import org.plank.analyzer.element.TypedGetExpr
import org.plank.analyzer.element.TypedGroupExpr
import org.plank.analyzer.element.TypedIdentPattern
import org.plank.analyzer.element.TypedIfBranch
import org.plank.analyzer.element.TypedIfExpr
import org.plank.analyzer.element.TypedInstanceExpr
import org.plank.analyzer.element.TypedIntAddExpr
import org.plank.analyzer.element.TypedIntDivExpr
import org.plank.analyzer.element.TypedIntEQExpr
import org.plank.analyzer.element.TypedIntGTEExpr
import org.plank.analyzer.element.TypedIntGTExpr
import org.plank.analyzer.element.TypedIntLTEExpr
import org.plank.analyzer.element.TypedIntLTExpr
import org.plank.analyzer.element.TypedIntMulExpr
import org.plank.analyzer.element.TypedIntNEQExpr
import org.plank.analyzer.element.TypedIntOperationExpr
import org.plank.analyzer.element.TypedIntSubExpr
import org.plank.analyzer.element.TypedMatchExpr
import org.plank.analyzer.element.TypedNamedTuplePattern
import org.plank.analyzer.element.TypedPattern
import org.plank.analyzer.element.TypedRefExpr
import org.plank.analyzer.element.TypedSetExpr
import org.plank.analyzer.element.TypedSizeofExpr
import org.plank.analyzer.element.TypedThenBranch
import org.plank.analyzer.infer.DoubleInfo
import org.plank.analyzer.infer.EnumInfo
import org.plank.analyzer.infer.FloatInfo
import org.plank.analyzer.infer.InlineVariable
import org.plank.analyzer.infer.IntInfo
import org.plank.analyzer.infer.LocalVariable
import org.plank.analyzer.infer.RankedVariable
import org.plank.analyzer.infer.Scope
import org.plank.analyzer.infer.StructInfo
import org.plank.syntax.element.text

fun ResolvedPlankFile.pretty(): String = buildString {
  appendLine("; File path: $path")
  paren("module ${module.text}")

  program.forEach { decl ->
    append(decl.pretty("", topLevel = true))
  }
}

fun TypedIfBranch.pretty(indent: String = ""): String = buildString {
  when (this@pretty) {
    is TypedThenBranch -> append(value.pretty(indent))
    is TypedBlockBranch -> paren {
      appendLine()
      stmts.forEach { stmt ->
        append(stmt.pretty("$indent  "))
      }
      append("$indent  ")
      append(value.pretty("$indent  "))
    }
  }
}

fun TypedPattern.pretty(indent: String = ""): String = buildString {
  when (this@pretty) {
    is TypedNamedTuplePattern -> paren {
      append(info.name.text)
      properties.forEach { pattern ->
        space()
        append(pattern.pretty(indent))
      }
    }
    is TypedIdentPattern -> append(name.text)
  }
}

fun TypedExpr.pretty(indent: String = ""): String = buildString {
  when (this@pretty) {
    is TypedAccessExpr -> append(name.text)
    is TypedGroupExpr -> paren(value.pretty(indent))
    is TypedCallExpr -> paren {
      prettyCall(indent, this@pretty)
    }
    is TypedSizeofExpr -> paren {
      append("sizeof ").append(ty)
    }
    is TypedRefExpr -> paren {
      append("ref ").append(value.pretty(indent))
    }
    is TypedDerefExpr -> paren {
      append("deref ").append(value.pretty(indent))
    }
    is TypedGetExpr -> paren {
      append("get ").append(receiver.pretty(indent)).space()
      append(member.text)
    }
    is TypedSetExpr -> paren {
      append("set ").append(receiver.pretty(indent)).space()
      append(member.text).space()
      prettyArgument(indent, value)
    }
    is TypedIfExpr -> paren {
      append("if ").append(cond.pretty(indent)).space()

      appendLine()
      append("$indent  ").append(thenBranch.pretty("$indent  "))

      if (elseBranch != null) {
        appendLine()
        append("$indent  ").append(elseBranch.pretty("$indent  "))
      }
    }
    is TypedAssignExpr -> paren {
      append("assign ")
      if (module != null) {
        append(module.name.text)
        space()
      }
      append(name.text).space()
      prettyArgument(indent, value)
    }
    is TypedInstanceExpr -> paren {
      append("inst ")
      append(info.name.text)

      arguments.entries.sortedBy { info.members.keys.indexOf(it.key) }.forEach { (_, expr) ->
        space()
        prettyArgument(indent, expr)
      }
    }
    is TypedBlockExpr -> paren {
      appendLine()
      stmts.forEach { stmt ->
        append(stmt.pretty("$indent  "))
      }

      append("$indent  ")
      append(value.pretty("$indent  "))
    }
    is TypedMatchExpr -> paren {
      val patternLength = patterns.keys
        .maxByOrNull { it.pretty("$indent  ").length }!!
        .pretty("$indent  ").length

      append("match ").append(subject.pretty(indent))

      patterns.forEach { (pattern, value) ->
        appendLine()
        append("$indent  ")
        paren {
          append(pattern.pretty("$indent  ").padEnd(patternLength)).space()
          append(value.pretty("$indent  "))
        }
      }
    }
    is TypedConstExpr -> when (val value = value) {
      is String -> append("\"$value\"")
      is Char -> append("'$value'")
      is Int -> append("${value}i32")
      is Short -> append("${value}i16")
      is Byte -> append("${value}i8")
      is UInt -> append("${value}u32")
      is UShort -> append("${value}u16")
      is UByte -> append("${value}u8")
      is Long -> append("${value}l")
      is Double -> append("${value}d")
      is Float -> append("${value}f")
      is Unit -> append("()")
      else -> append(value)
    }
    is TypedIntOperationExpr -> when (this@pretty) {
      is TypedIntAddExpr -> {}
      is TypedIntDivExpr -> {}
      is TypedIntEQExpr -> {}
      is TypedIntGTEExpr -> {}
      is TypedIntGTExpr -> {}
      is TypedIntLTEExpr -> {}
      is TypedIntLTExpr -> {}
      is TypedIntMulExpr -> {}
      is TypedIntNEQExpr -> {}
      is TypedIntSubExpr -> {}
    }
  }
}

fun ResolvedFunctionBody.pretty(indent: String = ""): String = buildString {
  append(indent)
  when (this@pretty) {
    is ResolvedCodeBody -> {
      appendLine("; Code body")
      stmts.forEach { stmt ->
        append(stmt.pretty(indent))
      }
      value?.let { value ->
        append(indent).append(value.pretty(indent))
      }
    }
    is ResolvedExprBody -> {
      appendLine("; Expr body")
      append(indent).append(expr.pretty(indent))
    }
    is ResolvedNoBody -> {
      appendLine("; No body")
      append(indent).paren("sorry!")
    }
  }
}

fun ResolvedStmt.pretty(indent: String = "", topLevel: Boolean = false): String = buildString {
  if (topLevel) {
    appendLine()
    appendLine()
  }

  append(indent)

  when (this@pretty) {
    is ResolvedExprStmt -> append(expr.pretty(indent))
    is ResolvedUseDecl -> paren {
      append("use ").append(module.name.text)
    }
    is ResolvedModuleDecl -> paren {
      append("mod ").append(name.text)
      appendLine()

      content.forEach { decl ->
        append(decl.pretty("$indent  "))
      }
    }
    is ResolvedStructDecl -> paren {
      val nameLength = members.keys.maxByOrNull { it.text.length }!!.text.length

      append("type ").append(name.text)

      members.forEach { (name, property) ->
        appendLine()
        append("$indent  ")
        paren {
          append(name.text.padEnd(nameLength)).space()
          paren(property.ty)
        }
      }
    }
    is ResolvedReturnStmt -> paren {
      when (value) {
        null -> append("ret")
        else -> append("ret ").append(value.pretty(indent))
      }
    }
    is ResolvedEnumDecl -> paren {
      val nameLength = members.keys.maxByOrNull { it.text.length }!!.text.length

      append("enum ").append(name.text).space()
      append(info.generics.text().joinToString(" ") { "'$it" })

      members.forEach { (name, value) ->
        appendLine()
        append("$indent  ")
        paren {
          append(name.text.padEnd(nameLength)).space()
          paren(value.scheme)
        }
      }
    }
    is ResolvedFunDecl -> paren {
      append("defun ").append(name.text).space()
      paren(scheme).space()
      bracket(" " + parameters.keys.text().joinToString(" ") + " ")
      appendLine()
      append(body.pretty("$indent  "))
    }
    is ResolvedLetDecl -> paren {
      append("def ")
      if (mutable) {
        append("mut ")
      }
      append(name.text).space()
      paren(ty)
      appendLine()
      append("$indent  ")
      append(value.pretty("$indent  "))
    }
  }

  if (!topLevel) {
    appendLine()
  }
}

fun StringBuilder.prettyArgument(indent: String, expr: TypedExpr) {
  paren {
    if (expr is TypedCallExpr) {
      prettyCall(indent, expr)
    } else {
      append(expr.pretty(indent))
    }

    append(" : ").append(expr.ty)
  }
}

fun StringBuilder.prettyCall(indent: String, expr: TypedCallExpr) {
  prettyCallee(indent, expr.callee)
  space()
  prettyArgument(indent, expr.argument)
}

fun StringBuilder.prettyCallee(indent: String, expr: TypedExpr) {
  when (expr) {
    is TypedCallExpr -> {
      prettyCallee(indent, expr.callee)
      space()
      prettyArgument(indent, expr.argument)
    }
    else -> append(expr.pretty(indent))
  }
}

fun Scope.pretty(indent: String = ""): String = buildString {
  val variableLength = variables.keys
    .maxByOrNull { it.text.length }
    ?.text?.length ?: -1

  expanded.forEach { scope ->
    append(indent)
    appendLine("expand ${scope.name.text}")
    appendLine()
  }

  tree.modules.forEach { (name, module) ->
    append(indent)
    appendLine("module ${name.text}")
    appendLine(module.scope.pretty("$indent  "))
  }

  types.forEach { (name, info) ->
    when (info) {
      is EnumInfo -> {
        val variantLength = info.members.keys
          .maxByOrNull { it.text.length }
          ?.text?.length ?: -1

        append(indent)
        append("enum ${name.text}")
        appendLine()
        info.members.values.forEach { member ->
          append("$indent  ")
          append("variant ${member.name.text.padEnd(variantLength)} : ${member.scheme}")

          appendLine()
        }
        appendLine()
      }
      is StructInfo -> {
        val variantLength = info.members.keys
          .maxByOrNull { it.text.length }
          ?.text?.length ?: -1

        append(indent)
        append("struct ${name.text}")
        appendLine()
        info.members.values.forEach { member ->
          append("$indent  ")
          append("member ${member.name.text.padEnd(variantLength)} : ${member.ty}")

          appendLine()
        }
        appendLine()
      }
      is DoubleInfo -> {
        append(indent)
        append("double ${name.text}")
        appendLine()
      }
      is FloatInfo -> {
        append(indent)
        append("float ${name.text}")
        appendLine()
      }
      is IntInfo -> {
        append(indent)
        append("int ")
        if (info.unsigned) {
          append("unsigned ")
        }
        append(name.text)
        appendLine()
      }
      else -> {}
    }
  }

  variables.forEach { (name, variable) ->
    append(indent)
    when (variable) {
      is InlineVariable -> append("inline ${name.text.padEnd(variableLength)} : ${variable.ty}")
      is LocalVariable -> append("local ${name.text.padEnd(variableLength)} : ${variable.ty}")
      is RankedVariable -> append("ranked ${name.text.padEnd(variableLength)} : ${variable.scheme}")
    }
    appendLine()
  }
}

private fun StringBuilder.lparen(): StringBuilder = append("(")
private fun StringBuilder.rparen(): StringBuilder = append(")")
private fun StringBuilder.paren(value: Any): StringBuilder = append("($value)")
private fun StringBuilder.bracket(value: Any): StringBuilder = append("[$value]")
private fun StringBuilder.space(): StringBuilder = append(" ")

private fun StringBuilder.paren(fn: StringBuilder.() -> Unit): StringBuilder {
  lparen()
  fn()
  rparen()
  return this
}
