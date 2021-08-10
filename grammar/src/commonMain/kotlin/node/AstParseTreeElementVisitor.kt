package com.lorenzoog.plank.grammar.node

import com.lorenzoog.plank.grammar.element.AccessExpr
import com.lorenzoog.plank.grammar.element.AccessTypeRef
import com.lorenzoog.plank.grammar.element.ArrayTypeRef
import com.lorenzoog.plank.grammar.element.AssignExpr
import com.lorenzoog.plank.grammar.element.CallExpr
import com.lorenzoog.plank.grammar.element.ConstExpr
import com.lorenzoog.plank.grammar.element.DerefExpr
import com.lorenzoog.plank.grammar.element.EnumDecl
import com.lorenzoog.plank.grammar.element.ErrorPlankElement
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.ExprStmt
import com.lorenzoog.plank.grammar.element.FunDecl
import com.lorenzoog.plank.grammar.element.FunctionTypeRef
import com.lorenzoog.plank.grammar.element.GetExpr
import com.lorenzoog.plank.grammar.element.GroupExpr
import com.lorenzoog.plank.grammar.element.IdentPattern
import com.lorenzoog.plank.grammar.element.Identifier
import com.lorenzoog.plank.grammar.element.IfExpr
import com.lorenzoog.plank.grammar.element.ImportDecl
import com.lorenzoog.plank.grammar.element.InstanceExpr
import com.lorenzoog.plank.grammar.element.LetDecl
import com.lorenzoog.plank.grammar.element.MatchExpr
import com.lorenzoog.plank.grammar.element.ModuleDecl
import com.lorenzoog.plank.grammar.element.NamedTuplePattern
import com.lorenzoog.plank.grammar.element.Pattern
import com.lorenzoog.plank.grammar.element.PointerTypeRef
import com.lorenzoog.plank.grammar.element.QualifiedPath
import com.lorenzoog.plank.grammar.element.QualifiedPathCons
import com.lorenzoog.plank.grammar.element.QualifiedPathNil
import com.lorenzoog.plank.grammar.element.RefExpr
import com.lorenzoog.plank.grammar.element.ReturnStmt
import com.lorenzoog.plank.grammar.element.SetExpr
import com.lorenzoog.plank.grammar.element.SizeofExpr
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.grammar.element.StructDecl
import com.lorenzoog.plank.grammar.element.TypeRef

internal object AstParseTreeElementVisitor :
  Expr.Visitor<ParseTreeElement>,
  Stmt.Visitor<ParseTreeElement>,
  TypeRef.Visitor<ParseTreeElement>,
  QualifiedPath.Visitor<ParseTreeElement>,
  Pattern.Visitor<ParseTreeElement>,
  Identifier.Visitor<ParseTreeElement> {
  override fun visitErrorElement(error: ErrorPlankElement): ParseTreeElement {
    return ParseTreeError(error.message)
  }

  override fun visitMatchExpr(expr: MatchExpr): ParseTreeElement {
    return ParseTreeNode("MatchExpr") {
      child(visit(expr.subject))

      expr.patterns.forEach { (pattern, expr) ->
        child(visit(pattern), visit(expr))
      }
    }
  }

  override fun visitIfExpr(expr: IfExpr): ParseTreeElement {
    return ParseTreeNode("IfExpr") {
      child(visit(expr.cond))
      child(visit(expr.thenBranch))

      expr.elseBranch?.let { elseBranch ->
        child(visit(elseBranch))
      }
    }
  }

  override fun visitConstExpr(expr: ConstExpr): ParseTreeElement {
    return ParseTreeNode("ConstExpr") {
      child(ParseTreeLeaf(expr.literal))
    }
  }

  override fun visitAccessExpr(expr: AccessExpr): ParseTreeElement {
    return ParseTreeNode("AccessExpr") {
      child(visit(expr.path))
    }
  }

  override fun visitCallExpr(expr: CallExpr): ParseTreeElement {
    return ParseTreeNode("CallExpr") {
      child(visit(expr.callee))

      expr.arguments.forEach {
        child(visit(it))
      }
    }
  }

  override fun visitAssignExpr(expr: AssignExpr): ParseTreeElement {
    return ParseTreeNode("AssignExpr") {
      child(visit(expr.path))
      child(visit(expr.value))
    }
  }

  override fun visitSetExpr(expr: SetExpr): ParseTreeElement {
    return ParseTreeNode("SetExpr") {
      child(visit(expr.receiver))
      child(visit(expr.property))
      child(visit(expr.value))
    }
  }

  override fun visitGetExpr(expr: GetExpr): ParseTreeElement {
    return ParseTreeNode("GetExpr") {
      child(visit(expr.receiver))
      child(visit(expr.property))
    }
  }

  override fun visitGroupExpr(expr: GroupExpr): ParseTreeElement {
    return ParseTreeNode("GroupExpr") {
      child(visit(expr.expr))
    }
  }

  override fun visitInstanceExpr(expr: InstanceExpr): ParseTreeElement {
    return ParseTreeNode("InstanceExpr") {
      child(visit(expr.struct))

      expr.arguments.forEach { (name, value) ->
        child(visit(name), visit(value))
      }
    }
  }

  override fun visitSizeofExpr(expr: SizeofExpr): ParseTreeElement {
    return ParseTreeNode("SizeofExpr") {
      child(visit(expr.type))
    }
  }

  override fun visitRefExpr(expr: RefExpr): ParseTreeElement {
    return ParseTreeNode("RefExpr") {
      child(visit(expr.expr))
    }
  }

  override fun visitDerefExpr(expr: DerefExpr): ParseTreeElement {
    return ParseTreeNode("DerefExpr") {
      child(visit(expr.expr))
    }
  }

  override fun visitNamedTuplePattern(pattern: NamedTuplePattern): ParseTreeElement {
    return ParseTreeNode("NamedTuplePattern") {
      child(visit(pattern.type))

      pattern.fields.forEach {
        child(visit(it))
      }
    }
  }

  override fun visitIdentPattern(pattern: IdentPattern): ParseTreeElement {
    return ParseTreeNode("IdentPattern") {
      child(visit(pattern.name))
    }
  }

  override fun visit(path: QualifiedPath): ParseTreeElement {
    return ParseTreeNode("QualifiedPath") {
      path.fullPath.forEach {
        child(visit(it))
      }
    }
  }

  override fun visitQualifiedPathCons(path: QualifiedPathCons): ParseTreeElement {
    error("Can not parse element from only cons of QualifiedPath")
  }

  override fun visitQualifiedPathNil(path: QualifiedPathNil): ParseTreeElement {
    error("Can not parse element from only nil of QualifiedPath")
  }

  override fun visitExprStmt(stmt: ExprStmt): ParseTreeElement {
    return ParseTreeNode("ExprStmt") {
      child(visit(stmt.expr))
    }
  }

  override fun visitReturnStmt(stmt: ReturnStmt): ParseTreeElement {
    return ParseTreeNode("ReturnStmt") {
      stmt.value?.let { value ->
        child(visit(value))
      }
    }
  }

  override fun visitImportDecl(decl: ImportDecl): ParseTreeElement {
    return ParseTreeNode("ImportDecl") {
      child(visit(decl.path))
    }
  }

  override fun visitModuleDecl(decl: ModuleDecl): ParseTreeElement {
    return ParseTreeNode("ModuleDecl") {
      child(visit(decl.path))

      decl.content.forEach {
        child(visit(it))
      }
    }
  }

  override fun visitEnumDecl(decl: EnumDecl): ParseTreeElement {
    return ParseTreeNode("EnumDecl") {
      child(visit(decl.name))

      decl.members.forEach { (name, types) ->
        child(visit(name))

        types.forEach { type ->
          child(visit(type))
        }
      }
    }
  }

  override fun visitStructDecl(decl: StructDecl): ParseTreeElement {
    return ParseTreeNode("StructDecl") {
      child(visit(decl.name))

      decl.properties.forEach { (mutable, name, type) ->
        child(ParseTreeLeaf(mutable.toString()))
        child(visit(name))
        child(visit(type))
      }
    }
  }

  override fun visitFunDecl(decl: FunDecl): ParseTreeElement {
    return ParseTreeNode("FunDecl") {
      child(visit(decl.name))

      decl.realParameters.forEach { (name, type) ->
        child(visit(name), visit(type))
      }

      decl.returnType?.let { returnType ->
        child(visit(returnType))
      }

      decl.body.forEach {
        child(visit(it))
      }
    }
  }

  override fun visitLetDecl(decl: LetDecl): ParseTreeElement {
    return ParseTreeNode("LetDecl") {
      child(visit(decl.name))
      child(ParseTreeLeaf(decl.mutable.toString()))

      decl.type?.let { type ->
        child(visit(type))
      }

      child(visit(decl.value))
    }
  }

  override fun visitAccessTypeRef(ref: AccessTypeRef): ParseTreeElement {
    return ParseTreeNode("AccessTypeRef") {
      child(visit(ref.path))
    }
  }

  override fun visitPointerTypeRef(ref: PointerTypeRef): ParseTreeElement {
    return ParseTreeNode("PointerTypeRef") {
      child(visit(ref.type))
    }
  }

  override fun visitArrayTypeRef(ref: ArrayTypeRef): ParseTreeElement {
    return ParseTreeNode("ArrayTypeRef") {
      child(visit(ref.type))
    }
  }

  override fun visitFunctionTypeRef(ref: FunctionTypeRef): ParseTreeElement {
    return ParseTreeNode("FunctionTypeRef") {
      ref.parameters.forEach { parameter ->
        child(visit(parameter))
      }

      child(visit(ref.returnType))
    }
  }

  override fun visitIdentifier(identifier: Identifier): ParseTreeElement {
    return ParseTreeNode("Identifier") {
      child(ParseTreeLeaf(identifier.text))
    }
  }
}
