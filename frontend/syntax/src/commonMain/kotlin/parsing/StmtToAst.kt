package org.plank.syntax.parsing

import org.plank.syntax.element.Decl
import org.plank.syntax.element.EnumDecl
import org.plank.syntax.element.ExprStmt
import org.plank.syntax.element.FunDecl
import org.plank.syntax.element.LetDecl
import org.plank.syntax.element.ModuleDecl
import org.plank.syntax.element.PlankFile
import org.plank.syntax.element.ReturnStmt
import org.plank.syntax.element.Stmt
import org.plank.syntax.element.StructDecl
import org.plank.syntax.element.UnitTypeRef
import org.plank.syntax.element.UseDecl
import org.plank.syntax.parser.PlankParser.DeclContext
import org.plank.syntax.parser.PlankParser.DeclStmtContext
import org.plank.syntax.parser.PlankParser.EnumDeclContext
import org.plank.syntax.parser.PlankParser.ExprStmtContext
import org.plank.syntax.parser.PlankParser.FunDeclContext
import org.plank.syntax.parser.PlankParser.InferLetDeclContext
import org.plank.syntax.parser.PlankParser.LetDeclContext
import org.plank.syntax.parser.PlankParser.ModuleDeclContext
import org.plank.syntax.parser.PlankParser.ReturnStmtContext
import org.plank.syntax.parser.PlankParser.StmtContext
import org.plank.syntax.parser.PlankParser.StructDeclContext
import org.plank.syntax.parser.PlankParser.UseDeclContext

fun StmtContext.stmtToAst(file: PlankFile): Stmt = when (this) {
  is DeclStmtContext -> value!!.declToAst(file)
  is ExprStmtContext -> ExprStmt(value!!.exprToAst(file), treeLoc(file))
  is ReturnStmtContext -> ReturnStmt(value?.exprToAst(file), treeLoc(file))
  else -> error("Unsupported stmt ${this::class.simpleName}")
}

fun DeclContext.declToAst(file: PlankFile): Decl = when (this) {
  is UseDeclContext -> UseDecl(path!!.pathToAst(file), treeLoc(file))

  is ModuleDeclContext -> ModuleDecl(
    path = path!!.pathToAst(file),
    content = findDecl().map { it.declToAst(file) },
    loc = treeLoc(file),
  )

  is InferLetDeclContext -> LetDecl(
    name = name!!.tokenToAst(file),
    mutable = MUTABLE() != null,
    type = null,
    value = value!!.exprToAst(file),
    loc = treeLoc(file),
  )

  is LetDeclContext -> LetDecl(
    name = name!!.tokenToAst(file),
    mutable = MUTABLE() != null,
    type = type!!.typeRefToAst(file),
    value = value!!.exprToAst(file),
    loc = treeLoc(file),
  )

  is StructDeclContext -> StructDecl(
    name = name!!.tokenToAst(file),
    properties = findProp().map { prop ->
      val mutable = prop.MUTABLE() != null

      StructDecl.Property(mutable, prop.name!!.tokenToAst(file), prop.type!!.typeRefToAst(file))
    },
    generics = names?.IDENTIFIER()?.map { it.terminalToAst(file) }?.toSet().orEmpty(),
    loc = treeLoc(file),
  )

  is EnumDeclContext -> EnumDecl(
    name = name!!.tokenToAst(file),
    members = findEnumMember().map { member ->
      EnumDecl.Member(
        name = member.name!!.tokenToAst(file),
        parameters = member.findTypeRef().map { it.typeRefToAst(file) },
      )
    },
    generics = names?.IDENTIFIER()?.map { it.terminalToAst(file) }?.toSet().orEmpty(),
    loc = treeLoc(file),
  )

  is FunDeclContext -> FunDecl(
    attributes = findAttr().map { it.attrToAst(file) },
    name = name!!.tokenToAst(file),
    body = body!!.bodyToAst(file),
    parameters = findParam().associate { param ->
      param.name!!.tokenToAst(file) to param.type!!.typeRefToAst(file)
    },
    returnType = returnType?.typeRefToAst(file) ?: UnitTypeRef(),
    loc = treeLoc(file),
  )

  else -> error("Unsupported decl ${this::class.simpleName}")
}
