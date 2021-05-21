package com.lorenzoog.plank.grammar.mapper

import com.lorenzoog.plank.grammar.element.Decl
import com.lorenzoog.plank.grammar.element.Decl.FunDecl.Modifier
import com.lorenzoog.plank.grammar.element.Expr
import com.lorenzoog.plank.grammar.element.Identifier
import com.lorenzoog.plank.grammar.element.Location
import com.lorenzoog.plank.grammar.element.Pattern
import com.lorenzoog.plank.grammar.element.PlankElement
import com.lorenzoog.plank.grammar.element.PlankFile
import com.lorenzoog.plank.grammar.element.Stmt
import com.lorenzoog.plank.grammar.element.TypeDef
import com.lorenzoog.plank.grammar.generated.PlankParser
import com.lorenzoog.plank.grammar.generated.PlankParser.ArrayTypeContext
import com.lorenzoog.plank.grammar.generated.PlankParser.AssignExprContext
import com.lorenzoog.plank.grammar.generated.PlankParser.BinaryExprContext
import com.lorenzoog.plank.grammar.generated.PlankParser.BooleanExprContext
import com.lorenzoog.plank.grammar.generated.PlankParser.CallExprContext
import com.lorenzoog.plank.grammar.generated.PlankParser.DeclContext
import com.lorenzoog.plank.grammar.generated.PlankParser.EnumDeclContext
import com.lorenzoog.plank.grammar.generated.PlankParser.ExprContext
import com.lorenzoog.plank.grammar.generated.PlankParser.ExprStmtContext
import com.lorenzoog.plank.grammar.generated.PlankParser.FunDeclContext
import com.lorenzoog.plank.grammar.generated.PlankParser.FunTypeContext
import com.lorenzoog.plank.grammar.generated.PlankParser.GenericAccessContext
import com.lorenzoog.plank.grammar.generated.PlankParser.GenericUseContext
import com.lorenzoog.plank.grammar.generated.PlankParser.GroupExprContext
import com.lorenzoog.plank.grammar.generated.PlankParser.IfExprContext
import com.lorenzoog.plank.grammar.generated.PlankParser.ImportDeclContext
import com.lorenzoog.plank.grammar.generated.PlankParser.InstanceExprContext
import com.lorenzoog.plank.grammar.generated.PlankParser.LetDeclContext
import com.lorenzoog.plank.grammar.generated.PlankParser.LogicalExprContext
import com.lorenzoog.plank.grammar.generated.PlankParser.MatchExprContext
import com.lorenzoog.plank.grammar.generated.PlankParser.MatchPatternContext
import com.lorenzoog.plank.grammar.generated.PlankParser.ModuleDeclContext
import com.lorenzoog.plank.grammar.generated.PlankParser.NameTypeContext
import com.lorenzoog.plank.grammar.generated.PlankParser.NativeFunDeclContext
import com.lorenzoog.plank.grammar.generated.PlankParser.PatternContext
import com.lorenzoog.plank.grammar.generated.PlankParser.PrimaryContext
import com.lorenzoog.plank.grammar.generated.PlankParser.ProgramContext
import com.lorenzoog.plank.grammar.generated.PlankParser.PtrContext
import com.lorenzoog.plank.grammar.generated.PlankParser.QualifiedPathContext
import com.lorenzoog.plank.grammar.generated.PlankParser.ReturnStmtContext
import com.lorenzoog.plank.grammar.generated.PlankParser.SizeofExprContext
import com.lorenzoog.plank.grammar.generated.PlankParser.StmtContext
import com.lorenzoog.plank.grammar.generated.PlankParser.StringExprContext
import com.lorenzoog.plank.grammar.generated.PlankParser.StructDeclContext
import com.lorenzoog.plank.grammar.generated.PlankParser.TypeDeclContext
import com.lorenzoog.plank.grammar.generated.PlankParser.TypeDefContext
import com.lorenzoog.plank.grammar.generated.PlankParser.UnaryExprContext
import com.lorenzoog.plank.grammar.generated.PlankParserBaseVisitor
import com.lorenzoog.plank.grammar.utils.location
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.tree.ErrorNode
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.antlr.v4.kotlinruntime.tree.RuleNode

class DescriptorMapper(
  private val file: PlankFile,
  violations: List<SyntaxViolation>,
) : PlankParserBaseVisitor<PlankElement>() {
  private val violations = violations.toMutableList()

  override fun visit(tree: ParseTree): PlankElement {
    try {
      return super.visit(tree)!!
    } catch (violation: SyntaxViolation) {
      violations += violation
    }

    return file.copy(
      program = emptyList(),
      violations = violations
    )
  }

  // program
  override fun visitProgram(ctx: ProgramContext): PlankFile {
    val moduleName = ctx.findFileModule()?.findQualifiedPath()?.text

    if (violations.isNotEmpty()) {
      return file.copy(
        moduleName = moduleName,
        program = emptyList(),
        violations = violations
      )
    }

    return file.copy(
      moduleName = moduleName,
      program = ctx.findDecl().map { visitDecl(it) },
      violations = violations,
    )
  }

  // typedef
  override fun visitTypeDef(ctx: TypeDefContext): TypeDef {
    val declContext = ctx.findArrayType()
      ?: ctx.findFunType()
      ?: ctx.findNameType()
      ?: ctx.findPtrType()
      ?: ctx.findGenericAccess()
      ?: ctx.findGenericUse()
      ?: throw ExpectingViolation("type definition", ctx.toString(), ctx.start.location)

    return visit(declContext) as TypeDef
  }

  override fun visitGenericAccess(ctx: GenericAccessContext): PlankElement {
    return TypeDef.GenericAccess(ctx.name!!.asIdentifier(), ctx.start.location)
  }

  override fun visitGenericUse(ctx: GenericUseContext): PlankElement {
    val arguments = ctx.findTypeDef().map { visitTypeDef(it) }
    return TypeDef.GenericUse(
      TypeDef.Name(ctx.name!!.asIdentifier(), ctx.name.location),
      arguments,
      ctx.GREATER()?.symbol.location
    )
  }

  override fun visitPtrType(ctx: PlankParser.PtrTypeContext): PlankElement {
    return TypeDef.Ptr(visitTypeDef(ctx.findTypeDef()!!), ctx.start.location)
  }

  override fun visitFunType(ctx: FunTypeContext): TypeDef {
    val parameters = ctx.children
      .orEmpty()
      .filterIsInstance<TypeDefContext>()
      .let { it.take(it.size - 1) }
      .map { visitTypeDef(it) }

    val returnType = visitTypeDef(ctx.returnType!!)

    return TypeDef.Function(parameters, returnType, ctx.start.location)
  }

  override fun visitNameType(ctx: NameTypeContext): TypeDef {
    return TypeDef.Name(ctx.name!!.asIdentifier(), ctx.start.location)
  }

  override fun visitArrayType(ctx: ArrayTypeContext): TypeDef {
    return TypeDef.Array(visitTypeDef(ctx.findTypeDef()!!), ctx.start.location)
  }

  // declarations
  override fun visitDecl(ctx: DeclContext): Decl {
    return visit(
      ctx.findLetDecl()
        ?: ctx.findFunDecl()
        ?: ctx.findTypeDecl()
        ?: ctx.findModuleDecl()
        ?: ctx.findImportDecl()
        ?: throw ExpectingViolation("declaration", ctx.toString(), ctx.start.location)
    ) as Decl
  }

  override fun visitImportDecl(ctx: ImportDeclContext): PlankElement {
    val name = ctx.name!!.asIdentifier()

    return Decl.ImportDecl(name, ctx.start.location)
  }

  override fun visitModuleDecl(ctx: ModuleDeclContext): PlankElement {
    val name = ctx.name!!.asIdentifier()
    val body = ctx.findDecl().map { visitDecl(it) }

    return Decl.ModuleDecl(name, body, ctx.start.location)
  }

  override fun visitLetDecl(ctx: LetDeclContext): Decl {
    val name = ctx.name!!.asIdentifier()
    val mutable = ctx.MUTABLE() != null
    val type = ctx.type?.let { visitTypeDef(it) }
    val value = visitExpr(ctx.value!!)

    return Decl.LetDecl(name, mutable, type, value, ctx.start.location)
  }

  override fun visitNativeFunDecl(ctx: NativeFunDeclContext): Decl {
    val header = ctx.findFunHeader()!!
    val type = header.findFunctionType()
    val name = header.name!!.asIdentifier()
    val parameters = header.findParameter().associate { it.name!! to visitTypeDef(it.type!!) }

    return Decl.FunDecl(listOf(Modifier.Native), name, type, emptyList(), parameters, type.location)
  }

  override fun visitFunDecl(ctx: FunDeclContext): Decl {
    ctx.findNativeFunDecl()?.let { return visitNativeFunDecl(it) }

    val header = ctx.findFunHeader()!!
    val type = header.findFunctionType()
    val name = header.name!!.asIdentifier()
    val body = ctx.findStmt().map { visitStmt(it) }
    val parameters = header.findParameter().associate { it.name!! to visitTypeDef(it.type!!) }

    return Decl.FunDecl(emptyList(), name, type, body, parameters, type.location)
  }

  override fun visitTypeDecl(ctx: TypeDeclContext): PlankElement {
    val name = ctx.name!!

    fun findEnumDecl(structCtx: EnumDeclContext): Decl.EnumDecl {
      val members = structCtx.findEnumMember().map { member ->
        val memberName = member.name!!
        val memberFields = member.findTypeDef().map { visitTypeDef(it) }

        Decl.EnumDecl.Member(memberName.asIdentifier(), memberFields)
      }

      return Decl.EnumDecl(name.asIdentifier(), members, ctx.start.location)
    }

    fun findStructDecl(structCtx: StructDeclContext): Decl.StructDecl {
      val fields = structCtx.findStructField().map { field ->
        val fieldMutable = field.MUTABLE() != null
        val fieldName = field.findParameter()!!.name!!
        val fieldType = visitTypeDef(field.findParameter()!!.type!!)

        Decl.StructDecl.Field(fieldMutable, fieldName.asIdentifier(), fieldType)
      }

      return Decl.StructDecl(name.asIdentifier(), fields, ctx.start.location)
    }

    ctx.findEnumDecl()?.let { return findEnumDecl(it) }
    ctx.findStructDecl()?.let { return findStructDecl(it) }

    throw ExpectingViolation("type declaration", ctx.toString(), ctx.start.location)
  }

  // statements
  override fun visitStmt(ctx: StmtContext): Stmt {
    val stmt = visit(
      ctx.findDecl()
        ?: ctx.findExprStmt()
        ?: ctx.findIfExpr()
        ?: ctx.findReturnStmt()
        ?: throw ExpectingViolation("statement", ctx.toString(), ctx.start.location)
    )

    return when (stmt) {
      is Expr -> Stmt.ExprStmt(stmt, stmt.location)
      else -> stmt as Stmt
    }
  }

  override fun visitExprStmt(ctx: ExprStmtContext): Stmt {
    val value = visitExpr(ctx.value!!)
    val location = ctx.start.location

    return Stmt.ExprStmt(value, location)
  }

  override fun visitReturnStmt(ctx: ReturnStmtContext): PlankElement {
    val value = visitExpr(ctx.value!!)
    val location = ctx.RETURN()?.symbol.location

    return Stmt.ReturnStmt(value, location)
  }

  // expressions
  override fun visitExpr(ctx: ExprContext): Expr {
    return visit(
      ctx.findIfExpr()
        ?: ctx.findAssignExpr()
        ?: ctx.findInstanceExpr()
        ?: ctx.findSizeofExpr()
        ?: ctx.findMatchExpr()
        ?: throw ExpectingViolation("expression", ctx.toString(), ctx.start.location)
    ) as Expr
  }

  override fun visitMatchExpr(ctx: MatchExprContext): Expr {
    val subject = visitExpr(ctx.subject!!)
    val patterns = ctx.children.orEmpty()
      .mapNotNull {
        when (it) {
          is MatchPatternContext -> {
            val pattern = mapPattern(it.findPattern()!!)
            val value = visitExpr(it.findExpr()!!)

            pattern to value
          }
          else -> null
        }
      }
      .associate { it }

    return Expr.Match(subject, patterns, ctx.start.location)
  }

  override fun visitSizeofExpr(ctx: SizeofExprContext): PlankElement {
    return Expr.Sizeof(ctx.type!!.asIdentifier(), ctx.SIZEOF()?.symbol.location)
  }

  override fun visitInstanceExpr(ctx: InstanceExprContext): PlankElement {
    return Expr.Instance(
      ctx.name!!.asIdentifier(),
      ctx.findInstanceArgument().associate { argument ->
        argument.IDENTIFIER()!!.symbol!! to visitExpr(argument.findExpr()!!)
      },
      ctx.LBRACE()?.symbol.location
    )
  }

  override fun visitIfExpr(ctx: IfExprContext): Expr {
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

  override fun visitAssignExpr(ctx: AssignExprContext): Expr {
    ctx.findLogicalExpr()?.let { return visitLogicalExpr(it) }

    val name = ctx.name!!.asIdentifier()
    val value = visitAssignExpr(ctx.findAssignExpr()!!)
    val location = ctx.EQUAL()?.symbol.location

    val receiver = ctx.findCallExpr()
    if (receiver != null) {
      return Expr.Set(visitCallExpr(receiver), name, value, location)
    }

    return Expr.Assign(name, value, location)
  }

  override fun visitLogicalExpr(ctx: LogicalExprContext): Expr {
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
        else -> {
          throw ExpectingViolation("logical operator", ctx.toString(), ctx.start.location)
        }
      },
      rhs = visitLogicalExpr(ctx.lhs!!),
      location = ctx.op.location
    )
  }

  override fun visitBinaryExpr(ctx: BinaryExprContext): Expr {
    ctx.findUnaryExpr()?.let { return visitUnaryExpr(it) }

    val lhs = visitBinaryExpr(ctx.lhs!!)
    val rhs = visitBinaryExpr(ctx.rhs!!)
    val location = ctx.op.location

    return Expr.Binary(
      lhs,
      op = when (ctx.op?.text) {
        "+" -> Expr.Binary.Operation.Add
        "-" -> Expr.Binary.Operation.Sub
        "*" -> Expr.Binary.Operation.Mul
        "/" -> Expr.Binary.Operation.Div
        "++" -> return Expr.Concat(lhs, rhs, location)
        else -> {
          throw ExpectingViolation("binary operator", ctx.toString(), ctx.start.location)
        }
      },
      rhs,
      location
    )
  }

  override fun visitUnaryExpr(ctx: UnaryExprContext): Expr {
    ctx.findCallExpr()?.let { return visitCallExpr(it) }

    return Expr.Unary(
      op = when (ctx.op?.text) {
        "!" -> Expr.Unary.Operation.Bang
        "-" -> Expr.Unary.Operation.Neg
        else -> {
          throw ExpectingViolation("unary operator", ctx.toString(), ctx.start.location)
        }
      },
      rhs = visitUnaryExpr(ctx.rhs!!),
      location = ctx.op.location
    )
  }

  override fun visitCallExpr(ctx: CallExprContext): Expr {
    val head = visit(ctx.access!!)
    val tail = ctx.children.orEmpty().drop(1)

    return tail.fold(head as Expr) { acc, next ->
      when (next) {
        is PlankParser.GetContext -> {
          Expr.Get(acc, next.IDENTIFIER()?.symbol!!.asIdentifier(), next.DOT()?.symbol.location)
        }
        is PlankParser.ArgumentsContext -> {
          Expr.Call(acc, next.findExpr().map { visitExpr(it) }, next.LPAREN()?.symbol.location)
        }
        else -> {
          throw ExpectingViolation("call arguments", ctx.toString(), ctx.start.location)
        }
      }
    }
  }

  override fun visitGroupExpr(ctx: GroupExprContext): Expr {
    return Expr.Group(visitExpr(ctx.value!!), ctx.start.location)
  }

  override fun visitBooleanExpr(ctx: BooleanExprContext): Expr {
    val value = when {
      ctx.TRUE() != null -> true
      ctx.FALSE() != null -> false
      else -> {
        throw ExpectingViolation("boolean", ctx.toString(), ctx.start.location)
      }
    }

    return Expr.Const(value, ctx.start.location)
  }

  override fun visitStringExpr(ctx: StringExprContext): Expr {
    val value = ctx.text.substring(1, ctx.text.length - 1)

    return Expr.Const(value, ctx.start.location)
  }

  override fun visitPtr(ctx: PtrContext): PlankElement {
    val reference = ctx.AMPERSTAND()
    val value = ctx.STAR()
    val expr = ctx.findExpr()

    if (expr != null && reference != null) {
      return Expr.Reference(visitExpr(expr), reference.symbol.location)
    }

    if (expr != null && value != null) {
      return Expr.Value(visitExpr(expr), reference?.symbol.location)
    }

    return visitPrimary(ctx.findPrimary()!!)
  }

  override fun visitPrimary(ctx: PrimaryContext): Expr {
    ctx.findGroupExpr()?.let { return visitGroupExpr(it) }
    ctx.findBooleanExpr()?.let { return visitBooleanExpr(it) }
    ctx.findStringExpr()?.let { return visitStringExpr(it) }

    val identifier = ctx.IDENTIFIER()
    if (identifier != null) {
      return Expr.Access(identifier.symbol!!.asIdentifier(), identifier.symbol.location)
    }

    val node = ctx.INT() ?: ctx.DECIMAL() ?: error("Invalid primary")
    val value =
      node.text.toIntOrNull()
        ?: node.text.toDoubleOrNull()
        ?: node.text

    return Expr.Const(value, node.symbol.location)
  }

  override fun visitErrorNode(node: ErrorNode): PlankElement? {
    println(node)
    println(node.payload)

    return super.visitErrorNode(node)
  }

  // utils
  private val Token?.location get() = location(file)

  private fun QualifiedPathContext.asIdentifier(): Identifier {
    val first = getChild(0) as? Token
    return Identifier(text, Location(first?.line ?: -1, first?.charPositionInLine ?: -1, file))
  }

  private fun RuleNode.asIdentifier(): Identifier {
    val first = getChild(0) as? Token
    return Identifier(text, Location(first?.line ?: -1, first?.charPositionInLine ?: -1, file))
  }

  private fun Token.asIdentifier(): Identifier {
    return Identifier(text!!, Location(line, charPositionInLine, file))
  }

  private fun PlankParser.FunHeaderContext.findFunctionType(): TypeDef.Function {
    val parameters = findParameter().map { visitTypeDef(it.type!!) }
    val location = start.location

    return TypeDef.Function(parameters, returnType?.let { visitTypeDef(it) }, location)
  }

  private fun mapPattern(patternCtx: PatternContext): Pattern {
    patternCtx.findIdentifierPattern()?.let { pattern ->
      val name = pattern.IDENTIFIER()?.symbol!!
      return Pattern.Ident(name.asIdentifier(), name.location)
    }
    patternCtx.findNamedTuplePattern()?.let { pattern ->
      val type = pattern.type!!.asIdentifier()
      val fields = pattern.findPattern().map(::mapPattern)

      return Pattern.NamedTuple(type, fields, pattern.start.location)
    }

    throw ExpectingViolation("pattern", patternCtx.toString(), patternCtx.start.location)
  }
}
