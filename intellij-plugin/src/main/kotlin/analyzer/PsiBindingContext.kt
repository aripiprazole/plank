package com.lorenzoog.jplank.intellijplugin.analyzer

import com.intellij.psi.PsiElement
import com.lorenzoog.jplank.analyzer.Builtin
import com.lorenzoog.jplank.analyzer.PlankType
import com.lorenzoog.plank.intellijplugin.psi.PlankArguments
import com.lorenzoog.plank.intellijplugin.psi.PlankArrayTypeDef
import com.lorenzoog.plank.intellijplugin.psi.PlankAssignExpr
import com.lorenzoog.plank.intellijplugin.psi.PlankCallExpr
import com.lorenzoog.plank.intellijplugin.psi.PlankCallableTypeDef
import com.lorenzoog.plank.intellijplugin.psi.PlankClassDecl
import com.lorenzoog.plank.intellijplugin.psi.PlankComparison
import com.lorenzoog.plank.intellijplugin.psi.PlankDecl
import com.lorenzoog.plank.intellijplugin.psi.PlankElseBranch
import com.lorenzoog.plank.intellijplugin.psi.PlankEquality
import com.lorenzoog.plank.intellijplugin.psi.PlankExpr
import com.lorenzoog.plank.intellijplugin.psi.PlankExprStmt
import com.lorenzoog.plank.intellijplugin.psi.PlankFactor
import com.lorenzoog.plank.intellijplugin.psi.PlankFunDecl
import com.lorenzoog.plank.intellijplugin.psi.PlankGenericAccessTypeDef
import com.lorenzoog.plank.intellijplugin.psi.PlankGenericTypeDef
import com.lorenzoog.plank.intellijplugin.psi.PlankGet
import com.lorenzoog.plank.intellijplugin.psi.PlankIdentifierExpr
import com.lorenzoog.plank.intellijplugin.psi.PlankIfExpr
import com.lorenzoog.plank.intellijplugin.psi.PlankImportDirective
import com.lorenzoog.plank.intellijplugin.psi.PlankImports
import com.lorenzoog.plank.intellijplugin.psi.PlankInstanceArguments
import com.lorenzoog.plank.intellijplugin.psi.PlankLetDecl
import com.lorenzoog.plank.intellijplugin.psi.PlankNameTypeDef
import com.lorenzoog.jplank.intellijplugin.psi.PlankNamedElement
import com.lorenzoog.plank.intellijplugin.psi.PlankNativeFunDecl
import com.lorenzoog.plank.intellijplugin.psi.PlankPointer
import com.lorenzoog.plank.intellijplugin.psi.PlankPointerTypeDef
import com.lorenzoog.plank.intellijplugin.psi.PlankPrimary
import com.lorenzoog.jplank.intellijplugin.psi.PlankPsiFile
import com.lorenzoog.plank.intellijplugin.psi.PlankReturnStmt
import com.lorenzoog.plank.intellijplugin.psi.PlankSizeofExpr
import com.lorenzoog.plank.intellijplugin.psi.PlankStmt
import com.lorenzoog.plank.intellijplugin.psi.PlankTerm
import com.lorenzoog.plank.intellijplugin.psi.PlankThenBranch
import com.lorenzoog.plank.intellijplugin.psi.PlankTypeDef
import com.lorenzoog.plank.intellijplugin.psi.PlankTypes
import com.lorenzoog.plank.intellijplugin.psi.PlankUnary
import com.lorenzoog.plank.intellijplugin.psi.impl.PlankPsiImplUtil
import pw.binom.Stack

class PsiBindingContext(
  val topScope: Scope = AnalyzerScope(name = "current", nested = false)
) {
  val annotations = mutableSetOf<CodeAnnotation>()
  private val scopes = Stack<Scope>()

  init {
    scopes.pushLast(topScope)
  }

  fun visit(element: PsiElement): PlankType {
    return when (element) {
      is PlankPsiFile -> {
        if (element.imports != null) {
          visit(element.imports)
        }

        visit(element.program)

        Builtin.Void
      }

      is PlankDecl -> visitDecl(element)
      is PlankClassDecl -> visitClassDecl(element)
      is PlankLetDecl -> visitLetDecl(element)
      is PlankFunDecl -> visitFunDecl(element)
      is PlankNativeFunDecl -> visitNativeFunDecl(element)

      is PlankStmt -> visitStmt(element)
      is PlankExprStmt -> visitExprStmt(element)
      is PlankReturnStmt -> visitReturnStmt(element)

      is PlankExpr -> visitExpr(element)
      is PlankAssignExpr -> visitAssignExpr(element)
      is PlankCallExpr -> visitCallExpr(element)
      is PlankIfExpr -> visitIfExpr(element)
      is PlankSizeofExpr -> visitSizeofExpr(element)
      is PlankIdentifierExpr -> visitIdentifierExpr(element)

      is PlankEquality -> visitEquality(element)
      is PlankFactor -> visitFactor(element)
      is PlankComparison -> visitComparison(element)
      is PlankTerm -> visitTerm(element)
      is PlankUnary -> visitUnary(element)
      is PlankPointer -> visitPointer(element)
      is PlankPrimary -> visitPrimary(element)

      is PlankTypeDef -> visitTypeDef(element)
      is PlankGenericTypeDef -> visitGenericTypeDef(element)
      is PlankGenericAccessTypeDef -> visitGenericAccessTypeDef(element)
      is PlankArrayTypeDef -> visitArrayTypeDef(element)
      is PlankNameTypeDef -> visitNameTypeDef(element)
      is PlankCallableTypeDef -> visitCallableTypeDef(element)
      is PlankPointerTypeDef -> visitPointerTypeDef(element)

      is PlankImports -> visitImports(element)
      is PlankImportDirective -> visitImportDirective(element)

      else -> Builtin.Void
    }
  }

  private fun visit(elements: List<PsiElement>): List<PlankType> {
    return elements.map { visit(it) }
  }

  private fun visit(elements: Array<out PsiElement>): List<PlankType> {
    return elements.map { visit(it) }
  }

  private fun visitImportDirective(importDirective: PlankImportDirective): PlankType {
    // TODO
    return Builtin.Void
  }

  private fun visitImports(imports: PlankImports): PlankType {
    visit(imports.children)

    return Builtin.Void
  }

  private fun visitDecl(decl: PlankDecl): PlankType {
    return when {
      decl.letDecl != null -> visitLetDecl(decl.letDecl!!)
      decl.funDecl != null -> visitFunDecl(decl.funDecl!!)
      decl.nativeFunDecl != null -> visitNativeFunDecl(decl.nativeFunDecl!!)
      decl.classDecl != null -> visitClassDecl(decl.classDecl!!)

      else -> Builtin.Void
    }
  }

  private fun visitClassDecl(classDecl: PlankClassDecl): PlankType {
    val struct = PlankType.Struct(
      classDecl.name,
      classDecl.fields.map { (name, field) ->
        val (mutable, type) = field

        PlankType.Struct.Field(mutable, name, visitTypeDef(type))
      }
    )

    currentScope.define(classDecl.name, struct)

    return Builtin.Void
  }

  private fun visitLetDecl(let: PlankLetDecl): PlankType {
    val actual = visit(let.expr)
    val expected = let.typeDef?.let { visit(it) } ?: actual

    if (!expected.isAssignableBy(actual)) {
      annotations += TypeViolation(expected, actual, let)
    }

    currentScope.declare(let.name, expected, let.mutable)

    return expected
  }

  private fun visitFunDecl(funDecl: PlankFunDecl): PlankType {
    val header = funDecl.funHeader
    val parameters = header.parameterList.associate { it.name to visit(it.typeDef) }
    val returnType = visit(header.typeDef)
    val name = header.name

    val type = PlankType.Callable(parameters.values.toList(), returnType)

    currentScope.declare(name, type)

    return scoped(name) { funScope ->
      parameters.forEach { (name, type) ->
        funScope.declare(name, type)
      }

      visit(funDecl.stmtList)

      annotations += funDecl.stmtList
        .filterIsInstance<PlankReturnStmt>()
        .filterNot { returnType.isAssignableBy(visit(it)) }
        .map { stmt -> TypeViolation(returnType, visit(stmt), stmt) }

      type
    }
  }

  private fun visitNativeFunDecl(funDecl: PlankNativeFunDecl): PlankType {
    val header = funDecl.funHeader
    val parameters = header.parameterList.associate { it.name to visit(it.typeDef) }
    val returnType = visit(header.typeDef)
    val name = header.name

    val type = PlankType.Callable(parameters.values.toList(), returnType)

    currentScope.declare(name, type)

    return scoped(name) { funScope ->
      parameters.forEach { (name, type) ->
        funScope.declare(name, type)
      }

      type
    }
  }

  private fun visitTypeDef(typeDef: PlankTypeDef): PlankType {
    return when {
      typeDef.arrayTypeDef != null -> visitArrayTypeDef(typeDef.arrayTypeDef!!)
      typeDef.callableTypeDef != null -> visitCallableTypeDef(typeDef.callableTypeDef!!)
      typeDef.genericTypeDef != null -> visitGenericTypeDef(typeDef.genericTypeDef!!)
      typeDef.nameTypeDef != null -> visitNameTypeDef(typeDef.nameTypeDef!!)
      typeDef.pointerTypeDef != null -> visitPointerTypeDef(typeDef.pointerTypeDef!!)
      typeDef.genericAccessTypeDef != null -> {
        visitGenericAccessTypeDef(typeDef.genericAccessTypeDef!!)
      }
      else -> Builtin.Void
    }
  }

  private fun visitArrayTypeDef(array: PlankArrayTypeDef): PlankType {
    return PlankType.Array(visitTypeDef(array.typeDef))
  }

  private fun visitNameTypeDef(name: PlankNameTypeDef): PlankType {
    annotations += TypeReferenceElement(name)

    if(name.name.startsWith("'")) { // generic access
      annotations += TypeReferenceElement(name)

      return Builtin.Any
    }

    return currentScope.findType(name.name) ?: run {
      annotations += UnresolvedTypeViolation(name.name, name)

      Builtin.Void
    }
  }

  private fun visitPointerTypeDef(pointer: PlankPointerTypeDef): PlankType {
    return PlankType.Pointer(visitTypeDef(pointer.typeDef))
  }

  private fun visitGenericAccessTypeDef(genericAccess: PlankGenericAccessTypeDef): PlankType {
    annotations += TypeReferenceElement(genericAccess)

    // TODO
    return Builtin.Any
  }

  private fun visitGenericTypeDef(generic: PlankGenericTypeDef): PlankType {
    return PlankType.Generic(
      visit(generic.receiver),
      visit(generic.typeDefList)
    )
  }

  private fun visitCallableTypeDef(callable: PlankCallableTypeDef): PlankType {
    val arguments = visit(callable.typeDefList.take(0))
    val returnType = visit(callable.typeDefList.last())

    return PlankType.Callable(arguments, returnType)
  }

  private fun visitPointer(pointer: PlankPointer): PlankType {
    if (pointer.expr != null) {
      return when (pointer.firstChild.text) {
        "&" -> visit(pointer.expr!!)
        "*" -> visit(pointer.expr!!).pointer
        else -> Builtin.Void
      }
    }

    return visitPrimary(pointer.primary!!)
  }

  private fun visitEquality(equality: PlankEquality): PlankType {
    val cmp = visitComparison(equality.lhs)
    return equality.rightmostOperands.entries.fold(cmp) { _, (_, next) ->
      visit(next)

      Builtin.Bool
    }
  }

  private fun visitComparison(comparison: PlankComparison): PlankType {
    val term = visitTerm(comparison.lhs)

    return comparison.rightmostOperands.entries.fold(term) { lhs, (_, rhs) ->
      val actual = visit(rhs)

      if (!Builtin.Numeric.isAssignableBy(lhs)) {
        annotations += TypeViolation(Builtin.Numeric, lhs, rhs)
      }

      if (!Builtin.Numeric.isAssignableBy(actual)) {
        annotations += TypeViolation(Builtin.Numeric, actual, rhs)
      }

      Builtin.Bool
    }
  }

  private fun visitTerm(term: PlankTerm): PlankType {
    val factor = visitFactor(term.lhs)

    return term.rightmostOperands.entries.fold(factor) { lhs, (op, rhs) ->
      val expected = when (op) {
        "++" -> Builtin.Char.pointer
        else -> Builtin.Numeric
      }
      val actual = visit(rhs)

      if (!expected.isAssignableBy(lhs)) {
        annotations += TypeViolation(Builtin.Numeric, lhs, rhs)
      }

      if (!expected.isAssignableBy(actual)) {
        annotations += TypeViolation(Builtin.Numeric, actual, rhs)
      }

      Builtin.Bool
    }
  }

  private fun visitFactor(factor: PlankFactor): PlankType {
    val unary = visitUnary(factor.lhs)

    return factor.rightmostOperands.entries.fold(unary) { lhs, (_, rhs) ->
      val expected = Builtin.Numeric
      val actual = visit(rhs)

      if (!expected.isAssignableBy(lhs)) {
        annotations += TypeViolation(Builtin.Numeric, lhs, rhs)
      }

      if (!expected.isAssignableBy(actual)) {
        annotations += TypeViolation(Builtin.Numeric, actual, rhs)
      }

      Builtin.Bool
    }
  }

  private fun visitUnary(unary: PlankUnary): PlankType {
    if (unary.callExpr != null) {
      return visitCallExpr(unary.callExpr!!)
    }

    val expected = when (unary.operator) {
      "!" -> Builtin.Bool
      "-" -> Builtin.Numeric
      else -> Builtin.Void
    }
    val actual = visit(unary.unary ?: return Builtin.Void)

    if (!expected.isAssignableBy(actual)) {
      annotations += TypeViolation(Builtin.Numeric, actual, unary)
    }

    return expected
  }

  private fun visitPrimary(primary: PlankPrimary): PlankType {
    if (primary.identifierExpr != null) {
      return visitIdentifierExpr(primary.identifierExpr!!)
    }

    val string = PlankPsiImplUtil.findChildOrNull(PlankTypes.STRING, primary)
    if (string != null) {
      return Builtin.Char.pointer
    }

    val int = PlankPsiImplUtil.findChildOrNull(PlankTypes.INT, primary)
    if (int != null) {
      return Builtin.Int
    }

    val decimal = PlankPsiImplUtil.findChildOrNull(PlankTypes.DECIMAL, primary)
    if (decimal != null) {
      return Builtin.Double
    }

    return Builtin.Any
  }

  private fun visitIdentifierExpr(identifier: PlankIdentifierExpr): PlankType {
    val name = identifier.name

    return currentScope.findVariable(name)?.type ?: currentScope.findType(name) ?: run {
      annotations += UnresolvedVariableViolation(name, identifier)

      Builtin.Any
    }
  }

  private fun visitExpr(expr: PlankExpr): PlankType {
    return when {
      expr.assignExpr != null -> visitAssignExpr(expr.assignExpr!!)
      expr.ifExpr != null -> visitIfExpr(expr.ifExpr!!)
      expr.sizeofExpr != null -> visitSizeofExpr(expr.sizeofExpr!!)

      else -> Builtin.Void
    }
  }

  private fun visitThenBranch(thenBranch: PlankThenBranch): PlankType {
    return visit(thenBranch.stmtList).lastOrNull() ?: Builtin.Void
  }

  private fun visitElseBranch(elseBranch: PlankElseBranch?): PlankType? {
    return visit((elseBranch ?: return Builtin.Void).stmtList).lastOrNull() ?: Builtin.Void
  }

  private fun visitIfExpr(ifExpr: PlankIfExpr): PlankType {
    val cond = visit(ifExpr.expr)

    if (!Builtin.Bool.isAssignableBy(cond)) {
      annotations += TypeViolation(Builtin.Bool, cond, ifExpr.expr)
    }

    val then = visitThenBranch(ifExpr.thenBranch)
    val orElse = visitElseBranch(ifExpr.elseBranch) ?: return Builtin.Void

    if (!then.isAssignableBy(orElse)) {
      annotations += TypeViolation(then, orElse, ifExpr)
    }

    return Builtin.Void
  }

  private fun visitSizeofExpr(sizeof: PlankSizeofExpr): PlankType {
    findStruct(sizeof.typeDef)

    return Builtin.Int
  }

  private fun visitAssignExpr(expr: PlankAssignExpr): PlankType {
    if (expr.equality != null) {
      return visitEquality(expr.equality!!)
    }

    val assign = expr.assign
    val set = expr.set

    val actual = visit(expr.value)
    val expected = when {
      assign != null -> {
        val name = assign.receiver.name
        val (mutable, type) = currentScope.findVariable(name) ?: return run {
          annotations += UnresolvedVariableViolation(name, assign)

          Builtin.Any
        }

        if (!mutable) {
          annotations += AssignImmutableViolation(name, assign)
        }

        type
      }
      set != null -> {
        val receiver = visit(set.receiver.receiver)
        val (mutable, name, type) = receiver[set.receiver.name] ?: return run {
          annotations += TypeViolation(Builtin.Any, Builtin.Void, set)

          Builtin.Any
        }

        if (!mutable) {
          annotations += AssignImmutableViolation(name, set)
        }

        type
      }

      else -> Builtin.Void
    }

    if (!expected.isAssignableBy(actual)) {
      annotations += TypeViolation(expected, actual, expr)
    }

    return expected
  }

  private fun visitCallExpr(call: PlankCallExpr): PlankType {
    val receiver = call.pointer

    val pointer = visitPointer(call.pointer)
    val callee = findCallee(call.pointer) ?: return pointer

    annotations += FunctionCallElement(call.pointer)

    val curlyArguments = call.children.filter { it is PlankArguments || it is PlankGet }

    return curlyArguments.fold(callee as PlankType) { acc, next ->
      when (next) {
        is PlankArguments -> {
          if (acc !is PlankType.Callable) {
            annotations += TypeViolation("callable", acc, next)

            return@fold Builtin.Any
          }

          next.children.forEachIndexed { i, element ->
            val expected = acc.parameters[i]
            val actual = visit(element)

            if (!expected.isAssignableBy(actual)) {
              annotations += TypeViolation(expected, actual, element)
            }
          }

          acc.returnType
        }

        is PlankGet -> {
          acc[next.name]?.type ?: run {
            annotations += TypeViolation(Builtin.Any, Builtin.Void, next)

            Builtin.Any
          }
        }

        is PlankInstanceArguments -> {
          val struct = findStruct(receiver, receiver.text) ?: return@fold Builtin.Any

          next.arguments.forEach { (name, value) ->
            val expected = struct[name]?.type ?: run {
              annotations += UnresolvedVariableViolation(name, value)

              return Builtin.Any
            }
            val actual = visit(value)

            if (!expected.isAssignableBy(actual)) {
              annotations += TypeViolation(expected, actual, value)
            }
          }

          struct
        }

        else -> Builtin.Void
      }
    }
  }

  private fun visitStmt(stmt: PlankStmt): PlankType {
    return when (val realStmt = stmt.firstChild) {
      is PlankExprStmt -> visitExprStmt(realStmt)
      is PlankReturnStmt -> visitReturnStmt(realStmt)
      is PlankDecl -> visitDecl(realStmt)
      else -> Builtin.Void
    }
  }

  private fun visitReturnStmt(stmt: PlankReturnStmt): PlankType {
    return visit(stmt.expr ?: return Builtin.Void)
  }

  private fun visitExprStmt(stmt: PlankExprStmt): PlankType {
    return visit(stmt.expr)
  }

  // utils
  private inline fun <T> scoped(
    name: String = "anonymous",
    nested: Boolean = true,
    enclosing: Scope? = scopes.peekLast(),
    body: (Scope) -> T
  ): T {
    val scope = AnalyzerScope(name, nested, enclosing)
    scopes.pushLast(scope)
    val result = body(scope)
    scopes.popLast()

    return result
  }

  private fun findStruct(element: PsiElement, name: String): PlankType.Struct? {
    return currentScope.findStruct(name).also {
      it ?: run {
        annotations += UnresolvedTypeViolation(name, element)
      }
    }
  }

  private fun findStruct(element: PsiElement): PlankType.Struct? {
    return when (element) {
      is PlankNamedElement -> findStruct(element, element.name)
      else -> {
        val actual = visit(element)
        if (actual is PlankType.Struct) {
          actual
        } else {
          annotations += TypeViolation("struct", actual, element)

          return null
        }
      }
    }
  }

  private fun findCallee(element: PsiElement): PlankType.Callable? {
    return when (element) {
      is PlankNamedElement -> currentScope.findFunction(element.name).also {
        if (it == null) {
          annotations += UnresolvedVariableViolation(element.name, element)
        }
      }
      else -> {
        val actual = visit(element)
        if (actual is PlankType.Callable) {
          actual
        } else {
          return null
        }
      }
    }
  }

  private inline val currentScope get() = scopes.peekLast()
}
