package com.lorenzoog.jplank.grammar

import com.lorenzoog.jplank.element.Decl
import com.lorenzoog.jplank.element.Decl.FunDecl.Modifier
import com.lorenzoog.jplank.element.Expr
import com.lorenzoog.jplank.element.ImportDirective
import com.lorenzoog.jplank.element.PkElement
import com.lorenzoog.jplank.element.PkFile
import com.lorenzoog.jplank.element.Stmt
import com.lorenzoog.jplank.element.TypeDef
import com.lorenzoog.jplank.grammar.generated.PlankParser
import com.lorenzoog.jplank.grammar.generated.PlankParserBaseVisitor
import com.lorenzoog.jplank.utils.location
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.tree.ErrorNode
import org.antlr.v4.kotlinruntime.tree.ParseTree

class DescriptorMapper(
  private val path: String,
  private val violations: List<SyntaxViolation>,
) : PlankParserBaseVisitor<PkElement>() {
  override fun visit(tree: ParseTree): PkElement {
    return super.visit(tree)!!
  }

  // program
  override fun visitProgram(ctx: PlankParser.ProgramContext): PkFile {
    if (violations.isNotEmpty()) {
      return PkFile(imports = emptyList(), program = emptyList(), violations = violations)
    }

    return PkFile(
      imports = ctx.findImports()?.findImportDirective().orEmpty().map {
        visitImportDirective(it)
      },
      program = ctx.findDecl().map { visitDecl(it) },
      violations = violations,
    )
  }

  override fun visitImportDirective(ctx: PlankParser.ImportDirectiveContext): ImportDirective {
    val module = ctx.module!!
    val location = ctx.IMPORT()?.symbol.location

    return ImportDirective.Module(module, location)
  }

  // typedef
  override fun visitTypeDef(ctx: PlankParser.TypeDefContext): TypeDef {
    val declContext = ctx.findArrayType()
      ?: ctx.findFunType()
      ?: ctx.findNameType()
      ?: error("Unsupported typedef")

    return visit(declContext) as TypeDef
  }

  override fun visitFunType(ctx: PlankParser.FunTypeContext): TypeDef {
    val parameters = ctx.children
      .orEmpty()
      .filterIsInstance<PlankParser.TypeDefContext>()
      .let { it.take(it.size - 1) }
      .map { visitTypeDef(it) }

    val returnType = visitTypeDef(ctx.returnType!!)

    return TypeDef.Function(parameters, returnType, ctx.start.location)
  }

  override fun visitNameType(ctx: PlankParser.NameTypeContext): TypeDef {
    return TypeDef.Name(ctx.name!!, ctx.start.location)
  }

  override fun visitArrayType(ctx: PlankParser.ArrayTypeContext): TypeDef {
    return TypeDef.Array(visitTypeDef(ctx.findTypeDef()!!), ctx.start.location)
  }

  // declarations
  override fun visitDecl(ctx: PlankParser.DeclContext): Decl {
    return visit(
      ctx.findLetDecl()
        ?: ctx.findFunDecl()
        ?: ctx.findClassDecl()
        ?: error("Unsupported decl")
    ) as Decl
  }

  override fun visitLetDecl(ctx: PlankParser.LetDeclContext): Decl {
    val name = ctx.name!!
    val mutable = ctx.MUTABLE() != null
    val type = ctx.type?.let { visitTypeDef(it) }
    val value = visitExpr(ctx.value!!)

    return Decl.LetDecl(name, mutable, type, value, ctx.start.location)
  }

  override fun visitNativeFunDecl(ctx: PlankParser.NativeFunDeclContext): Decl {
    val header = ctx.findFunHeader()!!
    val type = header.findFunctionType()
    val name = header.name!!
    val parameters = header.findParameter().associate { it.name!! to visitTypeDef(it.type!!) }

    return Decl.FunDecl(listOf(Modifier.Native), name, type, emptyList(), parameters, type.location)
  }

  override fun visitFunDecl(ctx: PlankParser.FunDeclContext): Decl {
    ctx.findNativeFunDecl()?.let { return visitNativeFunDecl(it) }

    val header = ctx.findFunHeader()!!
    val type = header.findFunctionType()
    val name = header.name!!
    val body = ctx.findStmt().map { visitStmt(it) }
    val parameters = header.findParameter().associate { it.name!! to visitTypeDef(it.type!!) }

    return Decl.FunDecl(emptyList(), name, type, body, parameters, type.location)
  }

  override fun visitClassDecl(ctx: PlankParser.ClassDeclContext): Decl {
    val name = ctx.name!!
    val fields = ctx.findClassField().map { field ->
      val fieldMutable = field.MUTABLE() != null
      val fieldName = field.findParameter()!!.name!!
      val fieldType = visitTypeDef(field.findParameter()!!.type!!)

      Decl.ClassDecl.Field(fieldMutable, fieldName, fieldType)
    }

    return Decl.ClassDecl(name, fields, ctx.start.location)
  }

  // statements
  override fun visitStmt(ctx: PlankParser.StmtContext): Stmt {
    val stmt = visit(
      ctx.findDecl()
        ?: ctx.findExprStmt()
        ?: ctx.findIfExpr()
        ?: ctx.findReturnStmt()
        ?: error("Unsupported stmt: ${ctx.start} ${ctx.stop} ${ctx.children}")
    )

    return when (stmt) {
      is Expr -> Stmt.ExprStmt(stmt, stmt.location)
      else -> stmt as Stmt
    }
  }

  override fun visitExprStmt(ctx: PlankParser.ExprStmtContext): Stmt {
    val value = visitExpr(ctx.value!!)
    val location = ctx.start.location

    return Stmt.ExprStmt(value, location)
  }

  override fun visitReturnStmt(ctx: PlankParser.ReturnStmtContext): PkElement {
    val value = visitExpr(ctx.value!!)
    val location = ctx.RETURN()?.symbol.location

    return Stmt.ReturnStmt(value, location)
  }

  // expressions
  override fun visitExpr(ctx: PlankParser.ExprContext): Expr {
    return visit(
      ctx.findIfExpr()
        ?: ctx.findAssignExpr()
        ?: ctx.findInstanceExpr()
        ?: ctx.findSizeofExpr()
        ?: error("Unsupported expr")
    ) as Expr
  }

  override fun visitSizeofExpr(ctx: PlankParser.SizeofExprContext): PkElement {
    return Expr.Sizeof(ctx.type!!, ctx.SIZEOF()?.symbol.location)
  }

  override fun visitInstanceExpr(ctx: PlankParser.InstanceExprContext): PkElement {
    return Expr.Instance(
      ctx.name!!,
      ctx.findInstanceArgument().associate { argument ->
        argument.IDENTIFIER()!!.symbol!! to visitExpr(argument.findExpr()!!)
      },
      ctx.LBRACE()?.symbol.location
    )
  }

  override fun visitIfExpr(ctx: PlankParser.IfExprContext): Expr {
    val cond = visitExpr(ctx.cond!!)

    val thenBranch = ctx.findThenBranch().let { thenBranch ->
      val exprBody = thenBranch?.findExpr()?.let {
        val expr = visitExpr(it)

        listOf(Stmt.ExprStmt(expr, expr.location))
      }

      exprBody ?: thenBranch?.findStmt().orEmpty().map { visitStmt(it) }
    }

    val elseBranch = ctx.findElseBranch().let { elseBranch ->
      val exprBody = elseBranch?.findExpr()?.let {
        val expr = visitExpr(it)

        listOf(Stmt.ExprStmt(expr, expr.location))
      }

      exprBody ?: elseBranch?.findStmt().orEmpty().map { visitStmt(it) }
    }

    val location = ctx.LPAREN()?.symbol.location

    return Expr.If(cond, thenBranch, elseBranch, location)
  }

  override fun visitAssignExpr(ctx: PlankParser.AssignExprContext): Expr {
    ctx.findLogicalExpr()?.let { return visitLogicalExpr(it) }

    val name = ctx.name!!
    val value = visitAssignExpr(ctx.findAssignExpr()!!)
    val location = ctx.EQUAL()?.symbol.location

    val receiver = ctx.findCallExpr()
    if (receiver != null) {
      return Expr.Set(visitCallExpr(receiver), name, value, location)
    }

    return Expr.Assign(name, value, location)
  }

  override fun visitLogicalExpr(ctx: PlankParser.LogicalExprContext): Expr {
    ctx.findBinaryExpr()?.let { return visitBinaryExpr(it) }

    return Expr.Logical(
      lhs = visitLogicalExpr(ctx.rhs!!),
      op = when (ctx.op?.text) {
        "<=" -> Expr.Logical.Operation.LessEquals
        "<" -> Expr.Logical.Operation.Less
        ">=" -> Expr.Logical.Operation.GreaterEquals
        ">" -> Expr.Logical.Operation.Greater
        "==" -> Expr.Logical.Operation.Equals
        "!=" -> Expr.Logical.Operation.NotEquals
        else -> error("Unsupported binary op")
      },
      rhs = visitLogicalExpr(ctx.lhs!!),
      location = ctx.op.location
    )
  }

  override fun visitBinaryExpr(ctx: PlankParser.BinaryExprContext): Expr {
    ctx.findUnaryExpr()?.let { return visitUnaryExpr(it) }

    return Expr.Binary(
      lhs = visitBinaryExpr(ctx.lhs!!),
      op = when (ctx.op?.text) {
        "+" -> Expr.Binary.Operation.Add
        "-" -> Expr.Binary.Operation.Sub
        "*" -> Expr.Binary.Operation.Mul
        "/" -> Expr.Binary.Operation.Div
        else -> error("Unsupported binary op")
      },
      rhs = visitBinaryExpr(ctx.rhs!!),
      location = ctx.op.location
    )
  }

  override fun visitUnaryExpr(ctx: PlankParser.UnaryExprContext): Expr {
    ctx.findCallExpr()?.let { return visitCallExpr(it) }

    return Expr.Unary(
      op = when (ctx.op?.text) {
        "!" -> Expr.Unary.Operation.Bang
        "-" -> Expr.Unary.Operation.Neg
        else -> error("Unsupported unary op")
      },
      rhs = visitUnaryExpr(ctx.rhs!!),
      location = ctx.op.location
    )
  }

  override fun visitCallExpr(ctx: PlankParser.CallExprContext): Expr {
    val head = visit(ctx.access!!)
    val tail = ctx.children.orEmpty().drop(1)

    return tail.fold(head as Expr) { acc, next ->
      when (next) {
        is PlankParser.GetContext -> {
          Expr.Get(acc, next.IDENTIFIER()?.symbol!!, next.DOT()?.symbol.location)
        }
        is PlankParser.ArgumentsContext -> {
          Expr.Call(acc, next.findExpr().map { visitExpr(it) }, next.LPAREN()?.symbol.location)
        }
        else -> error("Unsupported call argument")
      }
    }
  }

  override fun visitGroupExpr(ctx: PlankParser.GroupExprContext): Expr {
    return Expr.Group(visitExpr(ctx.value!!), ctx.start.location)
  }

  override fun visitBooleanExpr(ctx: PlankParser.BooleanExprContext): Expr {
    val value = when {
      ctx.TRUE() != null -> true
      ctx.FALSE() != null -> true
      else -> error("Unsupported boolean")
    }

    return Expr.Const(value, ctx.start.location)
  }

  override fun visitStringExpr(ctx: PlankParser.StringExprContext): Expr {
    val value = ctx.text.substring(1, ctx.text.length - 1)

    return Expr.Const(value, ctx.start.location)
  }

  override fun visitPrimary(ctx: PlankParser.PrimaryContext): Expr {
    ctx.findGroupExpr()?.let { return visitGroupExpr(it) }
    ctx.findBooleanExpr()?.let { return visitBooleanExpr(it) }
    ctx.findStringExpr()?.let { return visitStringExpr(it) }

    val identifier = ctx.IDENTIFIER()
    if (identifier != null) {
      return Expr.Access(identifier.symbol!!, identifier.symbol.location)
    }

    val node = ctx.INT() ?: error("Invalid primary")
    val value =
      node.text.toIntOrNull()
        ?: node.text.toDoubleOrNull()
        ?: node.text

    return Expr.Const(value, node.symbol.location)
  }

  override fun visitErrorNode(node: ErrorNode): PkElement? {
    println(node)
    println(node.payload)

    return super.visitErrorNode(node)
  }

  // utils
  private val Token?.location get() = location(path)

  private fun PlankParser.FunHeaderContext.findFunctionType(): TypeDef.Function {
    val parameters = findParameter().map { visitTypeDef(it.type!!) }
    val location = start.location

    return TypeDef.Function(parameters, returnType?.let { visitTypeDef(it) }, location)
  }
}
