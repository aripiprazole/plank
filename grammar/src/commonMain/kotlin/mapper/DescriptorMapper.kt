package com.gabrielleeg1.plank.grammar.mapper

import com.gabrielleeg1.plank.grammar.element.AccessExpr
import com.gabrielleeg1.plank.grammar.element.AccessTypeRef
import com.gabrielleeg1.plank.grammar.element.ArrayTypeRef
import com.gabrielleeg1.plank.grammar.element.AssignExpr
import com.gabrielleeg1.plank.grammar.element.CallExpr
import com.gabrielleeg1.plank.grammar.element.ConstExpr
import com.gabrielleeg1.plank.grammar.element.Decl
import com.gabrielleeg1.plank.grammar.element.DerefExpr
import com.gabrielleeg1.plank.grammar.element.EnumDecl
import com.gabrielleeg1.plank.grammar.element.ErrorExpr
import com.gabrielleeg1.plank.grammar.element.Expr
import com.gabrielleeg1.plank.grammar.element.ExprStmt
import com.gabrielleeg1.plank.grammar.element.FunDecl
import com.gabrielleeg1.plank.grammar.element.FunctionTypeRef
import com.gabrielleeg1.plank.grammar.element.GetExpr
import com.gabrielleeg1.plank.grammar.element.GroupExpr
import com.gabrielleeg1.plank.grammar.element.IdentPattern
import com.gabrielleeg1.plank.grammar.element.Identifier
import com.gabrielleeg1.plank.grammar.element.IfExpr
import com.gabrielleeg1.plank.grammar.element.ImportDecl
import com.gabrielleeg1.plank.grammar.element.InstanceExpr
import com.gabrielleeg1.plank.grammar.element.LetDecl
import com.gabrielleeg1.plank.grammar.element.Location
import com.gabrielleeg1.plank.grammar.element.MatchExpr
import com.gabrielleeg1.plank.grammar.element.ModuleDecl
import com.gabrielleeg1.plank.grammar.element.NamedTuplePattern
import com.gabrielleeg1.plank.grammar.element.Pattern
import com.gabrielleeg1.plank.grammar.element.PlankElement
import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.grammar.element.PointerTypeRef
import com.gabrielleeg1.plank.grammar.element.QualifiedPath
import com.gabrielleeg1.plank.grammar.element.RefExpr
import com.gabrielleeg1.plank.grammar.element.ReturnStmt
import com.gabrielleeg1.plank.grammar.element.SizeofExpr
import com.gabrielleeg1.plank.grammar.element.Stmt
import com.gabrielleeg1.plank.grammar.element.StructDecl
import com.gabrielleeg1.plank.grammar.element.TypeRef
import com.gabrielleeg1.plank.grammar.generated.PlankParser.AccessTypeRefContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.ArrayTypeRefContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.AssignExprContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.AssignExprHolderContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.AssignExprProviderContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.AssignValueHolderContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.BinaryExprContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.BinaryExprHolderContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.BinaryValueHolderContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.BooleanPrimaryContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.CallArgumentContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.CallExprContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.ConstExprContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.DecimalPrimaryContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.DeclContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.DeclStmtContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.DefinedLetDeclContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.DerefExprContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.EnumDeclContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.ExprContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.ExprStmtContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.FileContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.FileModuleContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.FunDeclContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.FunctionTypeRefContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.GetArgumentContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.GroupPrimaryContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.IdentPatternContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.IdentifierPrimaryContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.IfExprContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.ImportDeclContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.InferLetDeclContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.InstanceExprContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.IntPrimaryContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.LogicalExprContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.LogicalExprHolderContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.LogicalValueHolderContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.MatchExprContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.ModuleDeclContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.NamedTuplePatternContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.PatternContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.PointerTypeRefContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.QualifiedPathContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.RefExprContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.ReferenceContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.ReturnStmtContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.SizeofExprContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.StmtContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.StringPrimaryContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.StructDeclContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.TypeReferenceContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.UnaryExprContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.UnaryExprHolderContext
import com.gabrielleeg1.plank.grammar.generated.PlankParser.UnaryValueHolderContext
import com.gabrielleeg1.plank.grammar.generated.PlankParserBaseVisitor
import org.antlr.v4.kotlinruntime.RuleContext
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.misc.Interval
import org.antlr.v4.kotlinruntime.tree.ErrorNode
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.antlr.v4.kotlinruntime.tree.RuleNode
import org.antlr.v4.kotlinruntime.tree.TerminalNode

class DescriptorMapper(val file: PlankFile) : PlankParserBaseVisitor<PlankElement>() {
  override fun visit(tree: ParseTree): PlankElement? {
    return tree.accept(this)
  }

  override fun visitChildren(node: RuleNode): PlankElement? {
    return null
  }

  override fun visitErrorNode(node: ErrorNode): Expr {
    return ErrorExpr(node.text)
  }

  override fun visitTerminal(node: TerminalNode): Identifier {
    return Identifier(node.text, node.sourceInterval.location())
  }

  override fun visitFile(ctx: FileContext): PlankFile {
    return file.copy(program = ctx.findDecl().map { it.decl() })
  }

  override fun visitFileModule(ctx: FileModuleContext): QualifiedPath {
    val path = ctx.path ?: error("No path received in file module context")

    return path.qualifiedPath()
  }

  override fun visitAccessTypeRef(ctx: AccessTypeRefContext): TypeRef {
    val path = ctx.path ?: error("No path received in access type ref")

    return AccessTypeRef(path.qualifiedPath(), ctx.location())
  }

  override fun visitFunctionTypeRef(ctx: FunctionTypeRefContext): TypeRef {
    val parameters = ctx.findTypeReference().map { it.typeRef() }
    val returnType = ctx.returnType ?: error("No return type received in function type ref")

    return FunctionTypeRef(parameters, returnType.typeRef(), ctx.location())
  }

  override fun visitPointerTypeRef(ctx: PointerTypeRefContext): TypeRef {
    val typeRef = ctx.type ?: error("No type received in array type ref")

    return PointerTypeRef(typeRef.typeRef(), ctx.location())
  }

  override fun visitArrayTypeRef(ctx: ArrayTypeRefContext): TypeRef {
    val typeRef = ctx.type ?: error("No type received in array type ref")

    return ArrayTypeRef(typeRef.typeRef(), ctx.location())
  }

  override fun visitModuleDecl(ctx: ModuleDeclContext): Decl {
    val path = ctx.path ?: error("No path received in module decl context")
    val content = ctx.findDecl().map { it.decl() }

    return ModuleDecl(path.qualifiedPath(), content, ctx.location())
  }

  override fun visitStructDecl(ctx: StructDeclContext): Decl {
    val name = ctx.name ?: error("No name received in struct decl context")
    val properties = ctx.findProperty()
      .map { propertyCtx ->
        val parameter = propertyCtx.findParameter()
          ?: error("No parameter received in property context")

        val propertyMutable = when (propertyCtx.MUTABLE()) {
          null -> false
          else -> true
        }
        val propertyName = parameter.name ?: error("No name received in parameter context")
        val propertyType = parameter.type ?: error("No type received in parameter context")

        StructDecl.Property(propertyMutable, propertyName.identifier(), propertyType.typeRef())
      }

    return StructDecl(name.identifier(), properties, ctx.location())
  }

  override fun visitEnumDecl(ctx: EnumDeclContext): Decl {
    val name = ctx.name ?: error("No name received in enum decl context")
    val members = ctx.findEnumMember().map { memberCtx ->
      val memberName = memberCtx.name ?: error("No name received in enum member context")
      val memberParameters = memberCtx.findTypeReference()

      EnumDecl.Member(memberName.identifier(), memberParameters.map { it.typeRef() })
    }

    return EnumDecl(name.identifier(), members, ctx.location())
  }

  override fun visitQualifiedPath(ctx: QualifiedPathContext): QualifiedPath {
    return QualifiedPath(ctx.IDENTIFIER().reversed().map { it.identifier() }, ctx.location())
  }

  override fun visitDefinedLetDecl(ctx: DefinedLetDeclContext): Decl {
    val name = ctx.name ?: error("No name received in defined let decl context")
    val mutable = when (ctx.MUTABLE()) {
      null -> false
      else -> true
    }
    val type = ctx.type ?: error("No type received in defined let decl context")
    val value = ctx.value ?: error("No value received in defined let decl context")

    return LetDecl(name.identifier(), mutable, type.typeRef(), value.expr(), ctx.location())
  }

  override fun visitInferLetDecl(ctx: InferLetDeclContext): Decl {
    val name = ctx.name ?: error("No name received in infer let decl context")
    val mutable = when (ctx.MUTABLE()) {
      null -> false
      else -> true
    }
    val value = ctx.value ?: error("No value received in infer let decl context")

    return LetDecl(name.identifier(), mutable, null, value.expr(), ctx.location())
  }

  override fun visitImportDecl(ctx: ImportDeclContext): Decl {
    val path = ctx.path ?: error("No path received in import decl context")

    return ImportDecl(path.qualifiedPath(), ctx.location())
  }

  override fun visitFunDecl(ctx: FunDeclContext): Decl {
    val parameters = ctx.findParameter().associate { parameter ->
      val name = parameter.name ?: error("No parameter name received in parameter context")
      val type = parameter.type ?: error("No parameter type received in parameter context")

      name.identifier() to type.typeRef()
    }

    val body = ctx.findFunctionBody()?.findStmt().orEmpty().map {
      it.stmt()
    }

    val modifiers = ctx.findFunctionModifier().map { modifier ->
      when {
        modifier.NATIVE() != null -> FunDecl.Modifier.Native
        else -> error("No modifier received in function modifier context")
      }
    }

    val returnType = ctx.findFunctionReturn()
      ?.returnType
      ?: error("No return type received in fun decl context")
    val name = ctx.name ?: error("No name received in fun decl context")

    val type = FunctionTypeRef(parameters.values.toList(), returnType.typeRef(), ctx.location())

    return FunDecl(modifiers, name.identifier(), type, body, parameters, ctx.location())
  }

  override fun visitDeclStmt(ctx: DeclStmtContext): Decl {
    val decl = ctx.findDecl() ?: error("No decl received in decl stmt context")

    return decl.decl()
  }

  override fun visitExprStmt(ctx: ExprStmtContext): Stmt {
    val value = ctx.value ?: error("No value received in expr stmt context")

    return ExprStmt(value.expr(), ctx.location())
  }

  override fun visitReturnStmt(ctx: ReturnStmtContext): Stmt {
    val value = ctx.value ?: error("No value received in expr stmt context")

    return ReturnStmt(value.expr(), ctx.location())
  }

  override fun visitNamedTuplePattern(ctx: NamedTuplePatternContext): Pattern {
    val type = ctx.type ?: error("No type received in named tuple pattern context")
    val fields = ctx.findPattern()

    return NamedTuplePattern(type.qualifiedPath(), fields.map { it.pattern() }, ctx.location())
  }

  override fun visitIdentPattern(ctx: IdentPatternContext): Pattern {
    val name = ctx.name ?: error("No name received in ident pattern context")

    return IdentPattern(name.identifier(), ctx.location())
  }

  override fun visitMatchExpr(ctx: MatchExprContext): Expr {
    val subject = ctx.subject ?: error("No subject received in match expr context")
    val patterns = ctx.findMatchPattern().associate { case ->
      val pattern = case.findPattern() ?: error("No pattern received in match pattern context")
      val value = case.value ?: error("No value received in match pattern context")

      pattern.pattern() to value.expr()
    }

    return MatchExpr(subject.expr(), patterns, ctx.location())
  }

  override fun visitSizeofExpr(ctx: SizeofExprContext): Expr {
    val type = ctx.type ?: error("No type received in sizeof expr context")

    return SizeofExpr(type.typeRef(), ctx.location())
  }

  override fun visitInstanceExpr(ctx: InstanceExprContext): Expr {
    val struct = ctx.type ?: error("No type received in instance expr context")
    val arguments = ctx.findInstanceArgument().associate { argument ->
      val name = argument.name ?: error("No argument name received in instance expr context")
      val value = argument.value ?: error("No argument value received in instance expr context")

      name.identifier() to value.expr()
    }

    return InstanceExpr(struct.typeRef(), arguments, ctx.location())
  }

  override fun visitAssignExprProvider(ctx: AssignExprProviderContext): Expr {
    val value = ctx.findAssignExpr()
      ?: error("No assign expr received in assign expr holder context")

    return value.expr()
  }

  override fun visitIfExpr(ctx: IfExprContext): Expr {
    val cond = ctx.cond ?: error("No cond received in if expr context")
    val thenBranch = ctx.thenBranch ?: error("No thenBranch received in if expr context")
    val elseBranch = ctx.findElseBranch()

    return IfExpr(cond.expr(), thenBranch.expr(), elseBranch?.value?.expr(), ctx.location())
  }

  override fun visitAssignExprHolder(ctx: AssignExprHolderContext): Expr {
    val name = ctx.name ?: error("No name received in assign expr holder context")
    val value = ctx.value ?: error("No value received in assign expr holder context")

    return AssignExpr(name.identifier(), value.expr(), ctx.location())
  }

  override fun visitAssignValueHolder(ctx: AssignValueHolderContext): Expr {
    val value = ctx.value ?: error("No value received in assign value holder context")

    return value.expr()
  }

  override fun visitLogicalExprHolder(ctx: LogicalExprHolderContext): Expr {
    val op = ctx.op ?: error("No op received in logical expr context")
    val lhs = ctx.lhs ?: error("No lhs received in logical expr context")
    val rhs = ctx.rhs ?: error("No rhs received in logical expr context")

    return CallExpr(op.identifier().access(), listOf(lhs.expr(), rhs.expr()), ctx.location())
  }

  override fun visitLogicalValueHolder(ctx: LogicalValueHolderContext): Expr {
    val value = ctx.value ?: error("No value received in logical value holder context")

    return value.expr()
  }

  override fun visitBinaryExprHolder(ctx: BinaryExprHolderContext): Expr {
    val op = ctx.op ?: error("No op received in binary expr context")
    val lhs = ctx.lhs ?: error("No lhs received in binary expr context")
    val rhs = ctx.rhs ?: error("No rhs received in binary expr context")

    return CallExpr(op.identifier().access(), listOf(lhs.expr(), rhs.expr()), ctx.location())
  }

  override fun visitBinaryValueHolder(ctx: BinaryValueHolderContext): Expr {
    val value = ctx.value ?: error("No value received in binary value holder context")

    return value.expr()
  }

  override fun visitUnaryExprHolder(ctx: UnaryExprHolderContext): Expr {
    val op = ctx.op ?: error("No op received in unary expr context")
    val rhs = ctx.rhs ?: error("No rhs received in unary expr context")

    return CallExpr(op.identifier().access(), listOf(rhs.expr()), ctx.location())
  }

  override fun visitUnaryValueHolder(ctx: UnaryValueHolderContext): Expr {
    val value = ctx.value ?: error("No value received in unary value holder context")

    return visitCallExpr(value)
  }

  override fun visitCallExpr(ctx: CallExprContext): Expr {
    val callee = ctx.callee ?: error("No callee received in call expr context")

    return ctx.findArgumentFragment().fold(callee.expr()) { acc, next ->
      when (next) {
        is GetArgumentContext -> {
          val property = next.IDENTIFIER()
            ?: error("No property received in call expr context with get chain")

          GetExpr(acc, property.identifier(), next.location())
        }
        is CallArgumentContext -> {
          CallExpr(acc, next.findExpr().map { it.expr() }, next.location())
        }
        else -> error("No argument context received when folding in call expr context")
      }
    }
  }

  override fun visitRefExpr(ctx: RefExprContext): Expr {
    val value = ctx.value ?: error("No value received in ref expr context")

    return RefExpr(value.expr(), ctx.location())
  }

  override fun visitDerefExpr(ctx: DerefExprContext): Expr {
    val value = ctx.value ?: error("No value received in deref expr context")

    return DerefExpr(value.expr(), ctx.location())
  }

  override fun visitConstExpr(ctx: ConstExprContext): Expr {
    return when (val primary = ctx.findPrimary()) {
      is IntPrimaryContext -> visitIntPrimary(primary)
      is DecimalPrimaryContext -> visitDecimalPrimary(primary)
      is StringPrimaryContext -> visitStringPrimary(primary)
      is IdentifierPrimaryContext -> visitIdentifierPrimary(primary)
      is BooleanPrimaryContext -> visitBooleanPrimary(primary)
      is GroupPrimaryContext -> visitGroupPrimary(primary)
      null -> error("No primary received in const expr context")
      else -> error("No valid primary(${primary::class}) received in const expr context")
    }
  }

  override fun visitIntPrimary(ctx: IntPrimaryContext): Expr {
    val value = ctx.INT() ?: error("No int received in int primary context")

    return ConstExpr(value, ctx.location())
  }

  override fun visitDecimalPrimary(ctx: DecimalPrimaryContext): Expr {
    val value = ctx.DECIMAL() ?: error("No decimal received in decimal primary context")

    return ConstExpr(value, ctx.location())
  }

  override fun visitStringPrimary(ctx: StringPrimaryContext): Expr {
    val value = ctx.STRING() ?: error("No string received in string primary context")

    return ConstExpr(value, ctx.location())
  }

  override fun visitIdentifierPrimary(ctx: IdentifierPrimaryContext): Expr {
    val value = ctx.IDENTIFIER() ?: error("No identifier received in identifier primary context")

    return AccessExpr(QualifiedPath(value.identifier()), ctx.location())
  }

  override fun visitBooleanPrimary(ctx: BooleanPrimaryContext): Expr {
    val value = when {
      ctx.FALSE() != null -> false
      ctx.TRUE() != null -> true
      else -> error("No boolean value received in boolean primary context")
    }

    return ConstExpr(value, ctx.location())
  }

  override fun visitGroupPrimary(ctx: GroupPrimaryContext): Expr {
    val value = ctx.value ?: error("No expr received in group primary context")

    return GroupExpr(value.expr(), ctx.location())
  }

  // ast mapper utils
  private fun Identifier.access(): AccessExpr {
    return AccessExpr(QualifiedPath(this), location)
  }

  private fun QualifiedPath.access(): AccessExpr {
    return AccessExpr(this, location)
  }

  private fun Interval.location(): Location {
    return Location(a, b, file)
  }

  private fun RuleContext.location(): Location {
    return sourceInterval.location()
  }

  private fun QualifiedPathContext.qualifiedPath(): QualifiedPath {
    return visitQualifiedPath(this)
  }

  private fun TypeReferenceContext.typeRef(): TypeRef {
    return when (this) {
      is ArrayTypeRefContext -> visitArrayTypeRef(this)
      is PointerTypeRefContext -> visitPointerTypeRef(this)
      is FunctionTypeRefContext -> visitFunctionTypeRef(this)
      is AccessTypeRefContext -> visitAccessTypeRef(this)
      else -> error("Unknown type reference type context ${this::class.simpleName}")
    }
  }

  private fun TerminalNode.identifier(): Identifier {
    return Identifier(text, sourceInterval.location())
  }

  private fun Token.identifier(): Identifier {
    val text = text ?: error("No text received in Token")

    return Identifier(text, Location(startIndex, stopIndex, file))
  }

  private fun ExprContext.expr(): Expr {
    return when (this) {
      is IfExprContext -> visitIfExpr(this)
      is SizeofExprContext -> visitSizeofExpr(this)
      is InstanceExprContext -> visitInstanceExpr(this)
      is AssignExprProviderContext -> visitAssignExprProvider(this)
      is MatchExprContext -> visitMatchExpr(this)
      else -> error("Unknown type reference expr context ${this::class.simpleName}")
    }
  }

  private fun PatternContext.pattern(): Pattern {
    return when (this) {
      is IdentPatternContext -> visitIdentPattern(this)
      is NamedTuplePatternContext -> visitNamedTuplePattern(this)
      else -> error("Unknown type reference pattern context ${this::class.simpleName}")
    }
  }

  private fun AssignExprContext.expr(): Expr {
    return when (this) {
      is AssignExprHolderContext -> visitAssignExprHolder(this)
      is AssignValueHolderContext -> visitAssignValueHolder(this)
      else -> error("Unknown type reference assign expr context ${this::class.simpleName}")
    }
  }

  private fun LogicalExprContext.expr(): Expr {
    return when (this) {
      is LogicalExprHolderContext -> visitLogicalExprHolder(this)
      is LogicalValueHolderContext -> visitLogicalValueHolder(this)
      else -> error("Unknown type reference logical expr context ${this::class.simpleName}")
    }
  }

  private fun BinaryExprContext.expr(): Expr {
    return when (this) {
      is BinaryExprHolderContext -> visitBinaryExprHolder(this)
      is BinaryValueHolderContext -> visitBinaryValueHolder(this)
      else -> error("Unknown type reference binary expr context ${this::class.simpleName}")
    }
  }

  private fun UnaryExprContext.expr(): Expr {
    return when (this) {
      is UnaryExprHolderContext -> visitUnaryExprHolder(this)
      is UnaryValueHolderContext -> visitUnaryValueHolder(this)
      else -> error("Unknown type reference unary expr context ${this::class.simpleName}")
    }
  }

  private fun ReferenceContext.expr(): Expr {
    return when (this) {
      is ConstExprContext -> visitConstExpr(this)
      is DerefExprContext -> visitDerefExpr(this)
      is RefExprContext -> visitRefExpr(this)
      else -> error("Unknown type reference reference context ${this::class.simpleName}")
    }
  }

  private fun StmtContext.stmt(): Stmt {
    return when (this) {
      is DeclStmtContext -> visitDeclStmt(this)
      is ExprStmtContext -> visitExprStmt(this)
      is ReturnStmtContext -> visitReturnStmt(this)
      else -> error("Unknown type stmt context ${this::class.simpleName}")
    }
  }

  private fun DeclContext.decl(): Decl {
    return when (this) {
      is DefinedLetDeclContext -> visitDefinedLetDecl(this)
      is FunDeclContext -> visitFunDecl(this)
      is ImportDeclContext -> visitImportDecl(this)
      is InferLetDeclContext -> visitInferLetDecl(this)
      is ModuleDeclContext -> visitModuleDecl(this)
      is StructDeclContext -> visitStructDecl(this)
      is EnumDeclContext -> visitEnumDecl(this)
      else -> error("Unknown type decl context ${this::class.simpleName}")
    }
  }
}
