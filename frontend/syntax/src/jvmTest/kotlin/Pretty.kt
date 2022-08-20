package org.plank.syntax

import org.plank.syntax.element.AccessExpr
import org.plank.syntax.element.AccessTypeRef
import org.plank.syntax.element.ApplyTypeRef
import org.plank.syntax.element.AssignExpr
import org.plank.syntax.element.BlockBranch
import org.plank.syntax.element.BlockExpr
import org.plank.syntax.element.CallExpr
import org.plank.syntax.element.CodeBody
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.DerefExpr
import org.plank.syntax.element.EnumDecl
import org.plank.syntax.element.EnumVariantPattern
import org.plank.syntax.element.Expr
import org.plank.syntax.element.ExprBody
import org.plank.syntax.element.ExprStmt
import org.plank.syntax.element.FunDecl
import org.plank.syntax.element.FunctionBody
import org.plank.syntax.element.FunctionTypeRef
import org.plank.syntax.element.GenericTypeRef
import org.plank.syntax.element.GetExpr
import org.plank.syntax.element.GroupExpr
import org.plank.syntax.element.IdentPattern
import org.plank.syntax.element.IfBranch
import org.plank.syntax.element.IfExpr
import org.plank.syntax.element.InstanceExpr
import org.plank.syntax.element.LetDecl
import org.plank.syntax.element.MatchExpr
import org.plank.syntax.element.ModuleDecl
import org.plank.syntax.element.NoBody
import org.plank.syntax.element.Pattern
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.PointerTypeRef
import org.plank.syntax.element.RefExpr
import org.plank.syntax.element.ReturnStmt
import org.plank.syntax.element.SetExpr
import org.plank.syntax.element.SizeofExpr
import org.plank.syntax.element.Stmt
import org.plank.syntax.element.StructDecl
import org.plank.syntax.element.ThenBranch
import org.plank.syntax.element.TypeRef
import org.plank.syntax.element.UnitTypeRef
import org.plank.syntax.element.UseDecl

fun PlankFile.pretty(nesting: String = ""): String = buildString {
  appendLine("Module: $module")

  program.forEach { decl ->
    appendLine(decl.pretty(nesting))
    appendLine()
  }
}

fun Stmt.pretty(nesting: String = ""): String = buildString {
  when (this@pretty) {
    is UseDecl -> append(nesting).append("use ").append(path.text)
    is ExprStmt -> append(nesting).append(expr.pretty(nesting)).appendLine(";")

    is EnumDecl -> {
      append(nesting)
        .append("enum ")
        .append("${name.text} [")
        .append(generics.joinToString(", "))
        .append("] {")

      members.forEach { member ->
        append(nesting).append("  ")
          .append(member.name.text)
          .append("(")
          .append(member.parameters.joinToString(", ") { it.pretty() })
          .appendLine(")")
      }

      append(nesting).appendLine("}")
    }

    is FunDecl -> {
      val parametersString = parameters.entries.joinToString { (name, type) ->
        "${name.text}: ${type.pretty()}"
      }

      append(nesting)
        .append("fun ")
        .append(name.text)
        .append("(").append(parametersString).append(")")
        .append(" -> ").append(returnType.pretty()).append(" ")
        .append(body.pretty(nesting))
    }

    is LetDecl -> {
      append(nesting).append("let ").append(name.text)

      if (mutable) append("mutable ")
      if (type != null) append(" : ").append(type!!.pretty())

      append(" = ").appendLine(value.pretty(nesting))
    }

    is ModuleDecl -> {
      appendLine("module ${path.text}")
      content.forEach { decl ->
        append(decl.pretty("$nesting  "))
      }
    }

    is StructDecl -> {
      append(nesting).append("struct ").append(name.text).appendLine(" {")
      properties.forEach { (mutable, name, type) ->
        append(nesting)
        if (mutable) append("mutable ")
        append("${name.text}: ${type.pretty()}")
      }

      append(nesting).appendLine("}")
    }

    is ReturnStmt -> {
      append(nesting).append("return")

      if (value != null) {
        append(" ").append(value!!.pretty(nesting))
      }

      appendLine(";")
    }
  }
}

fun FunctionBody.pretty(nesting: String): String = buildString {
  when (this@pretty) {
    is ExprBody -> append("=> ${expr.pretty(nesting)}")
    is NoBody -> append("=> ???")
    is CodeBody -> {
      appendLine("{")
        .append(stmts.joinToString("\n") { it.pretty("$nesting  ") })
        .append(nesting).appendLine("}")
    }
  }
}

fun Expr.pretty(nesting: String): String = buildString {
  when (this@pretty) {
    is SizeofExpr -> append("sizeof").append(type.pretty())
    is DerefExpr -> append("*").append(value.pretty(nesting))
    is GetExpr -> append("${receiver.pretty(nesting)}.${property.text}")
    is GroupExpr -> append("(").append(value.pretty(nesting)).append(")")
    is RefExpr -> append("&").append(value.pretty(nesting))
    is SetExpr -> append(receiver.pretty(nesting))
      .append(".")
      .append(property.text)
      .append(" = ")
      .append(value.pretty(nesting))

    is AccessExpr -> {
      if (module != null) {
        append(module)
        append(".")
      }

      append(name.text)
    }

    is AssignExpr -> {
      if (module != null) {
        append(module)
        append(".")
      }

      append(name.text).append(" = ").append(value.pretty(nesting))
    }

    is BlockExpr -> {
      appendLine("{")
      stmts.forEach { stmt ->
        append(stmt.pretty("$nesting  "))
      }

      if (value != null) {
        append(nesting).append("  ").appendLine(value!!.pretty(nesting))
      }

      append(nesting).append("}")
    }

    is CallExpr -> {
      val argumentsString = arguments.joinToString(", ") { it.pretty(nesting) }

      append(callee.pretty(nesting))
        .append("(")
        .append(argumentsString)
        .append(")")
    }

    is ConstExpr -> when (value) {
      is String -> append("\"${value}\"")
      else -> append(value.toString())
    }

    is IfExpr -> {
      append("if ")
        .append(cond.pretty(nesting)).append(" then ")
        .append(thenBranch.pretty(nesting))

      if (elseBranch != null) {
        append(" else ").append(elseBranch!!.pretty(nesting))
      }
    }

    is InstanceExpr -> {
      val argumentsString = arguments.entries.joinToString { a ->
        "${a.key.text}: ${a.value.pretty(nesting)}"
      }

      append(type.pretty()).append("{")
        .append(argumentsString)
        .append("}")
    }

    is MatchExpr -> {
      append("match ").append(subject.pretty(nesting)).appendLine(" {")
      patterns.forEach { (pattern, expr) ->
        append(nesting)
          .append("  ")
          .append(pattern.pretty())
          .append(" => ")
          .append(expr.pretty("$nesting   "))
      }
      append(nesting).appendLine("}")
    }
  }
}

fun Pattern.pretty(): String = buildString {
  when (this@pretty) {
    is EnumVariantPattern -> {
      append(type.text).append("(")
        .append(properties.joinToString { it.pretty() })
        .append(")")
    }

    is IdentPattern -> append(name.text)
  }
}

fun IfBranch.pretty(nesting: String): String = buildString {
  when (this@pretty) {
    is BlockBranch -> {
      append("{")
      stmts.forEach { s ->
        append(s.pretty("$nesting  "))
      }
      appendLine(value?.pretty("$nesting  "))
    }

    is ThenBranch -> {
      append(value.pretty(nesting))
    }
  }
}

fun TypeRef.pretty(): String = buildString {
  when (this@pretty) {
    is UnitTypeRef -> append("()")
    is AccessTypeRef -> append(path.text)
    is FunctionTypeRef -> append("(${parameterType.pretty()}) -> ${returnType.pretty()}")
    is GenericTypeRef -> append("'${name.text}")
    is PointerTypeRef -> append("*${type.pretty()}")
    is ApplyTypeRef -> {
      append(function)
        .append(" [ ")
        .append(arguments.joinToString(" ") { it.pretty() })
        .append(" ]")
    }
  }
}
