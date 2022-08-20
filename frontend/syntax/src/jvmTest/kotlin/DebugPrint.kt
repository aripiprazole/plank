package org.plank.syntax

import org.plank.syntax.element.AccessExpr
import org.plank.syntax.element.AccessTypeRef
import org.plank.syntax.element.ApplyTypeRef
import org.plank.syntax.element.AssignExpr
import org.plank.syntax.element.BlockBranch
import org.plank.syntax.element.BlockExpr
import org.plank.syntax.element.CallExpr
import org.plank.syntax.element.ConstExpr
import org.plank.syntax.element.DerefExpr
import org.plank.syntax.element.EnumDecl
import org.plank.syntax.element.EnumVariantPattern
import org.plank.syntax.element.Expr
import org.plank.syntax.element.ExprStmt
import org.plank.syntax.element.FunDecl
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

fun PlankFile.debugPrint(nesting: String = ""): String = buildString {
  appendLine("Module: $module")

  program.forEach { decl ->
    appendLine(decl.debugPrint("$nesting  "))
  }
}

fun Stmt.debugPrint(nesting: String = ""): String = buildString {
  when (this@debugPrint) {
    is EnumDecl -> {
      appendLine("$nesting Enum ${name.text} [${generics.joinToString(", ")}]")
      members.forEach { m ->
        append("$nesting   Member ${m.name.text}")
          .appendLine(" (${m.parameters.joinToString(", ") { it.debugPrint() }})")
      }
    }

    is FunDecl -> {
      append("Fun ${name.text}")
        .append(" ( ")
        .append(
          parameters.entries.joinToString { e ->
            "${e.key.text} : ${e.value.debugPrint()}"
          },
        )
        .append(" ) -> ")
        .append(returnType.debugPrint())
        .appendLine()
    }

    is LetDecl -> {
      if (type != null) {
        append("$nesting Let ${name.text}: ${type!!.debugPrint()} = ")
          .appendLine(value.debugPrint(nesting))
      } else {
        appendLine("$nesting Let ${name.text} = ${value.debugPrint(nesting)}")
      }
    }

    is ModuleDecl -> {
      appendLine("Module ${path.text}")
      content.forEach { d ->
        append(d.debugPrint("$nesting  "))
      }
    }

    is StructDecl -> {
      appendLine("$nesting Struct ${name.text}")
      properties.forEach { p ->
        append("$nesting ${p.name.text}: ${p.type.debugPrint()} {Mutable=${p.mutable}}")
      }
    }

    is UseDecl -> {
      appendLine("Use ${path.text}")
    }

    is ExprStmt -> {
      appendLine("$nesting ${expr.debugPrint(nesting)};")
    }

    is ReturnStmt -> {
      appendLine("$nesting return ${value?.debugPrint(nesting)} ;")
    }
  }
}

fun Expr.debugPrint(nesting: String): String = buildString {
  when (this@debugPrint) {
    is AccessExpr -> {
      if (module == null) {
        append(name.text)
      } else {
        append("$module.${name.text}")
      }
    }

    is AssignExpr -> {
      if (module == null) {
        append(name.text).append(" = ").append(value.debugPrint(nesting))
      } else {
        append("$module.${name.text}").append(" = ").append(value.debugPrint(nesting))
      }
    }

    is BlockExpr -> {
      appendLine("{")
      stmts.forEach { s ->
        append(s.debugPrint("$nesting  "))
      }
      appendLine("$nesting   }")
    }

    is CallExpr -> append(callee.debugPrint(nesting))
      .append("(")
      .append(
        arguments.joinToString(", ") { it.debugPrint(nesting) },
      )
      .append(")")

    is ConstExpr -> when (value) {
      is String -> append("\"${value}\"")
      else -> append(value.toString())
    }

    is DerefExpr -> append("*").append(value.debugPrint(nesting))
    is GetExpr -> append("${receiver.debugPrint(nesting)}.${property.text}")
    is GroupExpr -> append("(").append(value.debugPrint(nesting)).append(")")
    is IfExpr -> {
      append("if ")
        .append(cond.debugPrint(nesting)).append(" then ")
        .append(thenBranch.debugPrint(nesting))

      if (elseBranch != null) {
        append(" else ").append(elseBranch!!.debugPrint(nesting))
      }
    }

    is InstanceExpr -> {
      append(type.debugPrint()).append("{")
        .append(
          arguments.entries.joinToString { a ->
            "${a.key.text}: ${a.value.debugPrint(nesting)}"
          },
        )
        .append("}")
    }

    is MatchExpr -> {
      append("match ").append(subject.debugPrint(nesting)).appendLine(" {")
      patterns.forEach { p ->
        append("$nesting   case ${p.key.debugPrint()} -> ${p.value.debugPrint("$nesting   ")}")
      }
      appendLine("$nesting }")
    }

    is RefExpr -> append("&").append(value.debugPrint(nesting))
    is SetExpr -> append(
      "${receiver.debugPrint(nesting)}.${property.text} = ${value.debugPrint(nesting)}",
    )

    is SizeofExpr -> append("sizeof ${type.debugPrint()}")
  }
}

fun Pattern.debugPrint(): String = buildString {
  when (this@debugPrint) {
    is EnumVariantPattern -> {
      append(type.text).append("(")
        .append(properties.joinToString { it.debugPrint() })
        .append(")")
    }

    is IdentPattern -> append(name.text)
  }
}

fun IfBranch.debugPrint(nesting: String): String = buildString {
  when (this@debugPrint) {
    is BlockBranch -> {
      append("{")
      stmts.forEach { s ->
        append(s.debugPrint("$nesting  "))
      }
      appendLine(value?.debugPrint("$nesting  "))
    }

    is ThenBranch -> {
      append(value.debugPrint(nesting))
    }
  }
}

fun TypeRef.debugPrint(): String = buildString {
  when (this@debugPrint) {
    is AccessTypeRef -> append(path.text)
    is ApplyTypeRef -> {
      append(function)
        .append(" [ ")
        .append(arguments.joinToString(" ") { it.debugPrint() }).append(" ]")
    }

    is FunctionTypeRef -> {
      append("(${parameterType.debugPrint()}) -> ${returnType.debugPrint()}")
    }

    is GenericTypeRef -> {
      append("'${name.text}")
    }

    is PointerTypeRef -> {
      append("*${type.debugPrint()}")
    }

    is UnitTypeRef -> {
      append("Unit")
    }
  }
}
