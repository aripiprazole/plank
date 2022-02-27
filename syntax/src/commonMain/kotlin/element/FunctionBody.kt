package org.plank.syntax.element

sealed interface FunctionBody : PlankElement {
  interface Visitor<T> {
    fun visitFunctionBody(body: FunctionBody): T = body.accept(this)

    fun visitNoBody(body: NoBody): T
    fun visitExprBody(body: ExprBody): T
    fun visitCodeBody(body: CodeBody): T
  }

  fun <T> accept(visitor: Visitor<T>): T
}

data class NoBody(override val location: Location = Location.Generated) : FunctionBody {
  override fun <T> accept(visitor: FunctionBody.Visitor<T>): T {
    return visitor.visitNoBody(this)
  }
}

data class ExprBody(val expr: Expr, override val location: Location = Location.Generated) : FunctionBody {
  override fun <T> accept(visitor: FunctionBody.Visitor<T>): T {
    return visitor.visitExprBody(this)
  }
}

data class CodeBody(
  val stmts: List<Stmt>,
  val value: Expr?,
  override val location: Location = Location.Generated,
) : FunctionBody {
  override fun <T> accept(visitor: FunctionBody.Visitor<T>): T {
    return visitor.visitCodeBody(this)
  }
}
