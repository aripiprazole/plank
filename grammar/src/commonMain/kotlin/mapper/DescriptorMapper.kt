package com.gabrielleeg1.plank.grammar.mapper

import com.gabrielleeg1.plank.grammar.element.*
import com.gabrielleeg1.plank.parser.PlankParser.*
import com.gabrielleeg1.plank.parser.PlankParserBaseVisitor
import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.RuleContext
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.misc.Interval
import org.antlr.v4.kotlinruntime.tree.ErrorNode
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.antlr.v4.kotlinruntime.tree.RuleNode
import org.antlr.v4.kotlinruntime.tree.TerminalNode

class DescriptorMapper(val file: PlankFile) : PlankParserBaseVisitor<PlankElement>() {
  override fun visit(tree: ParseTree): PlankElement? = tree.accept(this)
  override fun visitChildren(node: RuleNode): PlankElement? = null
  override fun visitErrorNode(node: ErrorNode): Expr = ErrorExpr(node.text)
  override fun visitTerminal(node: TerminalNode): Identifier =
    Identifier(node.text, node.sourceInterval.location)

  override fun visitFile(ctx: FileContext): PlankFile {
    return file.copy(
      moduleName = ctx.findModule()?.let(::visitModule),
      program = ctx.findDecl().map(::visitDecl),
    )
  }

  override fun visitStructDecl(ctx: StructDeclContext): StructDecl {
    return StructDecl(
      visitToken(ctx.name!!),
      ctx.findProp().map { prop ->
        val mutable = prop.MUTABLE() != null

        StructDecl.Property(mutable, visitToken(prop.name!!), visitTypeRef(prop.type!!))
      },
      ctx.location
    )
  }

  override fun visitEnumDecl(ctx: EnumDeclContext): EnumDecl {
    return EnumDecl(
      visitToken(ctx.name!!),
      ctx.findEnumMember().map { member ->
        EnumDecl.Member(visitToken(member.name!!), member.findTypeRef().map(::visitTypeRef))
      },
      ctx.location,
    )
  }

  override fun visitModuleDecl(ctx: ModuleDeclContext): ModuleDecl {
    return ModuleDecl(visitQualifiedPath(ctx.path!!), ctx.findDecl().map(::visitDecl), ctx.location)
  }

  override fun visitImportDecl(ctx: ImportDeclContext): ImportDecl {
    return ImportDecl(visitQualifiedPath(ctx.path!!), ctx.location)
  }

  override fun visitFunDecl(ctx: FunDeclContext): FunDecl {
    val parameters = ctx.findParam().associate { param ->
      visitToken(param.name!!) to visitTypeRef(param.type!!)
    }

    val returnType = ctx.returnType?.let(::visitTypeRef) ?: UnitTypeRef()
    val functionType = parameters.values.reversed()
      .fold(returnType) { acc, next ->
        FunctionTypeRef(next, acc, returnType, parameters, null, next.location)
      } as? FunctionTypeRef
      ?: FunctionTypeRef(UnitTypeRef(), returnType, returnType, parameters, null, ctx.location)

    return FunDecl(
      attributes = ctx.findAttr().map(::visitAttr),
      name = visitToken(ctx.name!!),
      type = functionType,
      body = ctx.findStmt().map(::visitStmt),
      location = ctx.location
    )
  }

  override fun visitInferLetDecl(ctx: InferLetDeclContext): LetDecl {
    return LetDecl(
      name = visitToken(ctx.name!!),
      mutable = ctx.MUTABLE() != null,
      type = null,
      value = visitExpr(ctx.value!!),
      location = ctx.location
    )
  }

  override fun visitLetDecl(ctx: LetDeclContext): LetDecl {
    return LetDecl(
      name = visitToken(ctx.name!!),
      mutable = ctx.MUTABLE() != null,
      type = visitTypeRef(ctx.type!!),
      value = visitExpr(ctx.value!!),
      location = ctx.location
    )
  }

  private fun visitDecl(ctx: DeclContext): Decl = when (ctx) {
    is StructDeclContext -> visitStructDecl(ctx)
    is EnumDeclContext -> visitEnumDecl(ctx)
    is ModuleDeclContext -> visitModuleDecl(ctx)
    is ImportDeclContext -> visitImportDecl(ctx)
    is FunDeclContext -> visitFunDecl(ctx)
    is InferLetDeclContext -> visitInferLetDecl(ctx)
    is LetDeclContext -> visitLetDecl(ctx)
    else -> error("Unsupported decl ${ctx::class.simpleName}")
  }

  override fun visitDeclStmt(ctx: DeclStmtContext): Decl {
    return visitDecl(ctx.value!!)
  }

  override fun visitExprStmt(ctx: ExprStmtContext): ExprStmt {
    return ExprStmt(visitExpr(ctx.value!!), ctx.location)
  }

  override fun visitReturnStmt(ctx: ReturnStmtContext): ReturnStmt {
    return ReturnStmt(ctx.value?.let(::visitExpr), ctx.location)
  }

  private fun visitStmt(ctx: StmtContext): Stmt = when (ctx) {
    is DeclStmtContext -> visitDeclStmt(ctx)
    is ExprStmtContext -> visitExprStmt(ctx)
    is ReturnStmtContext -> visitReturnStmt(ctx)
    else -> error("Unsupported stmt ${ctx::class.simpleName}")
  }

  override fun visitAssignExpr(ctx: AssignExprContext): AssignExpr {
    return AssignExpr(visitToken(ctx.name!!), visitExpr(ctx.value!!), ctx.location)
  }

  override fun visitSetExpr(ctx: SetExprContext): SetExpr {
    val property = ctx.findArg().fold(visitExpr(ctx.receiver!!), ::callFold)
      as? GetExpr ?: error("Receiver must be a GetExpr when setting up a variable")

    return SetExpr(property.receiver, property.property, visitExpr(ctx.value!!), ctx.location)
  }

  override fun visitBinaryExpr(ctx: BinaryExprContext): CallExpr {
    return CallExpr(
      callee = visitToken(ctx.op!!).asAccessExpr(),
      arguments = listOf(visitExpr(ctx.lhs!!), visitExpr(ctx.rhs!!)),
      location = ctx.location
    )
  }

  override fun visitUnaryExpr(ctx: UnaryExprContext): CallExpr {
    return CallExpr(
      callee = visitToken(ctx.op!!).asAccessExpr(),
      arguments = listOf(visitExpr(ctx.rhs!!)),
      location = ctx.location
    )
  }

  override fun visitCallExpr(ctx: CallExprContext): Expr {
    return ctx.findArg().fold(visitExpr(ctx.callee!!), ::callFold)
  }

  override fun visitInstanceExpr(ctx: InstanceExprContext): InstanceExpr {
    return InstanceExpr(
      visitTypeRef(ctx.type!!),
      ctx.findInstanceArg().associate { arg ->
        visitToken(arg.name!!) to visitExpr(arg.value!!)
      },
      ctx.location
    )
  }

  override fun visitIfExpr(ctx: IfExprContext): IfExpr {
    return IfExpr(
      visitExpr(ctx.cond!!),
      visitExpr(ctx.thenBranch!!),
      ctx.elseBranch?.let(this::visitExpr),
      ctx.location
    )
  }

  override fun visitSizeofExpr(ctx: SizeofExprContext): SizeofExpr {
    return SizeofExpr(visitTypeRef(ctx.type!!), ctx.location)
  }

  override fun visitMatchExpr(ctx: MatchExprContext): MatchExpr {
    return MatchExpr(
      visitExpr(ctx.subject!!),
      ctx.findMatchPattern().associate {
        visitPattern(it.key!!) to visitExpr(it.value!!)
      },
      ctx.location
    )
  }

  private fun visitExpr(ctx: ExprContext): Expr = when (ctx) {
    is AssignExprContext -> visitAssignExpr(ctx)
    is SetExprContext -> visitSetExpr(ctx)
    is BinaryExprContext -> visitBinaryExpr(ctx)
    is UnaryExprContext -> visitUnaryExpr(ctx)
    is CallExprContext -> visitCallExpr(ctx)
    is InstanceExprContext -> visitInstanceExpr(ctx)
    is IfExprContext -> visitIfExpr(ctx)
    is SizeofExprContext -> visitSizeofExpr(ctx)
    is MatchExprContext -> visitMatchExpr(ctx)
    else -> error("Unsupported expr ${ctx::class.simpleName}")
  }

  override fun visitRefExpr(ctx: RefExprContext): RefExpr {
    return RefExpr(visitExpr(ctx.value!!), ctx.location)
  }

  override fun visitDerefExpr(ctx: DerefExprContext): DerefExpr {
    return DerefExpr(visitExpr(ctx.value!!), ctx.location)
  }

  override fun visitIntExpr(ctx: IntExprContext): ConstExpr {
    return ConstExpr(ctx.value!!.text!!.toInt(), ctx.location)
  }

  override fun visitDecimalExpr(ctx: DecimalExprContext): ConstExpr {
    return ConstExpr(ctx.value!!.text!!.toDouble(), ctx.location)
  }

  override fun visitStringExpr(ctx: StringExprContext): ConstExpr {
    return ConstExpr(
      ctx.value!!.text!!.substring(1, ctx.value!!.text!!.length - 1),
      ctx.location
    )
  }

  override fun visitAccessExpr(ctx: AccessExprContext): AccessExpr {
    return AccessExpr(QualifiedPath(visitToken(ctx.value!!)), ctx.location)
  }

  override fun visitTrueExpr(ctx: TrueExprContext): ConstExpr {
    return ConstExpr(true, ctx.location)
  }

  override fun visitFalseExpr(ctx: FalseExprContext): ConstExpr {
    return ConstExpr(false, ctx.location)
  }

  override fun visitGroupExpr(ctx: GroupExprContext): GroupExpr {
    return GroupExpr(visitExpr(ctx.value!!), ctx.location)
  }

  private fun visitExpr(ctx: PrimaryContext): Expr = when (ctx) {
    is RefExprContext -> visitRefExpr(ctx)
    is DerefExprContext -> visitDerefExpr(ctx)
    is IntExprContext -> visitIntExpr(ctx)
    is DecimalExprContext -> visitDecimalExpr(ctx)
    is StringExprContext -> visitStringExpr(ctx)
    is AccessExprContext -> visitAccessExpr(ctx)
    is TrueExprContext -> visitTrueExpr(ctx)
    is FalseExprContext -> visitFalseExpr(ctx)
    is GroupExprContext -> visitGroupExpr(ctx)
    else -> error("Unsupported primary ${ctx::class.simpleName}")
  }

  override fun visitNamedTuplePattern(ctx: NamedTuplePatternContext): NamedTuplePattern {
    return NamedTuplePattern(
      visitQualifiedPath(ctx.type!!),
      ctx.findPattern().map(::visitPattern),
      ctx.location,
    )
  }

  override fun visitIdentPattern(ctx: IdentPatternContext): IdentPattern {
    return IdentPattern(visitToken(ctx.name!!), ctx.location)
  }

  private fun visitPattern(ctx: PatternContext): Pattern = when (ctx) {
    is NamedTuplePatternContext -> visitNamedTuplePattern(ctx)
    is IdentPatternContext -> visitIdentPattern(ctx)
    else -> error("Unsupported pattern ${ctx::class.simpleName}")
  }

  override fun visitAttrIntExpr(ctx: AttrIntExprContext): AttributeExpr<Int> {
    return IntAttributeExpr(ctx.value!!.text!!.toInt(), ctx.location)
  }

  override fun visitAttrDecimalExpr(ctx: AttrDecimalExprContext): AttributeExpr<Double> {
    return DecimalAttributeExpr(ctx.value!!.text!!.toDouble(), ctx.location)
  }

  override fun visitAttrStringExpr(ctx: AttrStringExprContext): AttributeExpr<String> {
    return StringAttributeExpr(
      ctx.value!!.text!!.substring(1, ctx.value!!.text!!.length - 1),
      ctx.location
    )
  }

  override fun visitAttrAccessExpr(ctx: AttrAccessExprContext): AttributeExpr<Identifier> {
    return AccessAttributeExpr(visitToken(ctx.value!!), ctx.location)
  }

  override fun visitAttrTrueExpr(ctx: AttrTrueExprContext): AttributeExpr<Boolean> {
    return BoolAttributeExpr(true, ctx.location)
  }

  override fun visitAttrFalseExpr(ctx: AttrFalseExprContext): AttributeExpr<Boolean> {
    return BoolAttributeExpr(false, ctx.location)
  }

  private fun visitAttrExpr(ctx: AttrExprContext): AttributeExpr<*> = when (ctx) {
    is AttrIntExprContext -> visitAttrIntExpr(ctx)
    is AttrDecimalExprContext -> visitAttrDecimalExpr(ctx)
    is AttrStringExprContext -> visitAttrStringExpr(ctx)
    is AttrAccessExprContext -> visitAttrAccessExpr(ctx)
    is AttrTrueExprContext -> visitAttrTrueExpr(ctx)
    is AttrFalseExprContext -> visitAttrFalseExpr(ctx)
    else -> error("Unsupported attr expr ${ctx::class.simpleName}")
  }

  override fun visitAttr(ctx: AttrContext): Attribute {
    return Attribute(visitToken(ctx.name!!), ctx.findAttrExpr().map(::visitAttrExpr), ctx.location)
  }

  override fun visitFunctionTypeRef(ctx: FunctionTypeRefContext): FunctionTypeRef {
    return FunctionTypeRef(
      visitTypeRef(ctx.parameter!!),
      visitTypeRef(ctx.returnType!!),
      location = ctx.location,
    )
  }

  private fun visitTypeRef(ctx: TypeRefContext): TypeRef = when (ctx) {
    is FunctionTypeRefContext -> visitFunctionTypeRef(ctx)
    is PrimaryTypeRefContext -> visitTypeRef(ctx.value!!)
    else -> error("Unsupported type ref ${ctx::class.simpleName}")
  }

  override fun visitAccessTypeRef(ctx: AccessTypeRefContext): AccessTypeRef {
    return AccessTypeRef(visitQualifiedPath(ctx.path!!), ctx.location)
  }

  override fun visitArrayTypeRef(ctx: ArrayTypeRefContext): ArrayTypeRef {
    return ArrayTypeRef(visitTypeRef(ctx.type!!), ctx.location)
  }

  override fun visitPointerTypeRef(ctx: PointerTypeRefContext): PointerTypeRef {
    return PointerTypeRef(visitTypeRef(ctx.type!!), ctx.location)
  }

  override fun visitGroupTypeRef(ctx: GroupTypeRefContext): TypeRef {
    return visitTypeRef(ctx.type!!)
  }

  private fun visitTypeRef(ctx: TypePrimaryContext): TypeRef = when (ctx) {
    is AccessTypeRefContext -> visitAccessTypeRef(ctx)
    is ArrayTypeRefContext -> visitArrayTypeRef(ctx)
    is PointerTypeRefContext -> visitPointerTypeRef(ctx)
    is GroupTypeRefContext -> visitGroupTypeRef(ctx)
    else -> error("Unsupported primary type ref ${ctx::class.simpleName}")
  }

  override fun visitModule(ctx: ModuleContext): QualifiedPath {
    return visitQualifiedPath(ctx.path!!)
  }

  override fun visitQualifiedPath(ctx: QualifiedPathContext): QualifiedPath {
    return QualifiedPath(ctx.IDENTIFIER().map(::visitTerminal), ctx.location)
  }

  private fun visitToken(token: Token): Identifier {
    val text = token.text ?: error("No text received in Token")

    return Identifier(text, Location(token.startIndex, token.stopIndex, file))
  }

  private val Interval.location
    get() = Location(a, b, file)
  private val RuleContext.location get() = sourceInterval.location
  private val ParserRuleContext.location
    get() = Location(start!!.startIndex, stop!!.stopIndex, file)

  private fun Identifier.asAccessExpr(): AccessExpr {
    return AccessExpr(QualifiedPath(this), location)
  }

  private fun callFold(acc: Expr, next: ArgContext): Expr {
    return when (next) {
      is GetArgContext -> GetExpr(acc, visitToken(next.name!!), next.location)
      is CallArgContext -> next.findExpr().fold(acc) { callee, arg ->
        CallExpr(callee, listOf(visitExpr(arg)), arg.location)
      }
      else -> error("Unsupported arg ${next::class.simpleName}")
    }
  }
}
