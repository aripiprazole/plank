package org.plank.analyzer.checker

import org.plank.analyzer.element.TypedIntAddExpr
import org.plank.analyzer.element.TypedIntDivExpr
import org.plank.analyzer.element.TypedIntEQExpr
import org.plank.analyzer.element.TypedIntGTEExpr
import org.plank.analyzer.element.TypedIntGTExpr
import org.plank.analyzer.element.TypedIntLTEExpr
import org.plank.analyzer.element.TypedIntLTExpr
import org.plank.analyzer.element.TypedIntMulExpr
import org.plank.analyzer.element.TypedIntNEQExpr
import org.plank.analyzer.element.TypedIntSubExpr
import org.plank.analyzer.infer.boolTy
import org.plank.analyzer.infer.charTy
import org.plank.analyzer.infer.i16Ty
import org.plank.analyzer.infer.i32Ty
import org.plank.analyzer.infer.i8Ty
import org.plank.syntax.element.Identifier
import org.plank.syntax.element.toIdentifier

object GlobalScope : Scope() {
  override val name: Identifier = "Global".toIdentifier()
  override val enclosing: Scope? = null

  /**
   * Init compiler-defined functions
   */
  init {
    createTyInfo(IntInfo(this, "Char", charTy, 8))
    createTyInfo(IntInfo(this, "Bool", boolTy, 8))
    createTyInfo(DoubleInfo(this))
    createTyInfo(FloatInfo(this))

    createTyInfo(IntInfo(this, "Int8", i8Ty, 8))
    createTyInfo(IntInfo(this, "Int16", i16Ty, 16))
    createTyInfo(IntInfo(this, "Int32", i32Ty, 32))

    // Add default binary operators
    declareInline("+", i32Ty, i32Ty, i32Ty) { (a, b) -> TypedIntAddExpr(a, b) }
    declareInline("-", i32Ty, i32Ty, i32Ty) { (a, b) -> TypedIntSubExpr(a, b) }
    declareInline("*", i32Ty, i32Ty, i32Ty) { (a, b) -> TypedIntMulExpr(a, b) }
    declareInline("/", i32Ty, i32Ty, i32Ty) { (a, b) -> TypedIntDivExpr(a, b) }

    // Add default logical operators
    declareInline("==", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntEQExpr(a, b) }
    declareInline("!=", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntNEQExpr(a, b) }
    declareInline(">=", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntGTEExpr(a, b) }
    declareInline(">", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntGTExpr(a, b) }
    declareInline("<=", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntLTEExpr(a, b) }
    declareInline("<", boolTy, i32Ty, i32Ty) { (a, b) -> TypedIntLTExpr(a, b) }
  }

  override fun enclose(scope: Scope): Scope = this

  override fun toString(): String = "Global"
}
