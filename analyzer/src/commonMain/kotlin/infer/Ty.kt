package org.plank.analyzer.infer

import org.plank.syntax.element.AccessTypeRef
import org.plank.syntax.element.ApplyTypeRef
import org.plank.syntax.element.FunctionTypeRef
import org.plank.syntax.element.GenericTypeRef
import org.plank.syntax.element.PointerTypeRef
import org.plank.syntax.element.TypeRef
import org.plank.syntax.element.UnitTypeRef

sealed interface Ty {
  override fun toString(): String
}

data class ConstTy(val name: String) : Ty {
  override fun toString(): String = name
}

data class VarTy(val name: String) : Ty {
  override fun toString(): String = "'$name"
}

data class PtrTy(val arg: Ty) : Ty {
  override fun toString(): String = "*$arg"
}

data class FunTy(val parameterTy: Ty, val returnTy: Ty) : Ty {
  override fun toString(): String = when (parameterTy) {
    is FunTy -> "($parameterTy) -> $returnTy"
    else -> "$parameterTy -> $returnTy"
  }

  fun nest(index: Int): Ty {
    var i = 0
    var current = returnTy

    while (index > i) {
      if (current is FunTy) {
        current = current.returnTy
      }
      i++
    }

    return current
  }
}

fun FunTy(returnTy: Ty, parameters: Collection<Ty>): FunTy =
  parameters
    .reversed()
    .ifEmpty { listOf(unitTy) }
    .fold(returnTy) { acc, ty -> FunTy(ty, acc) } as FunTy

data class AppTy(val fn: Ty, val arg: Ty) : Ty {
  override fun toString(): String = "($fn $arg)"
}

val undefTy: Ty = ConstTy("!")
val unitTy: Ty = ConstTy("()")
val boolTy: Ty = ConstTy("Bool")

val charTy: Ty = ConstTy("Char")

val i8Ty: Ty = ConstTy("Int8")
val i16Ty: Ty = ConstTy("Int16")
val i32Ty: Ty = ConstTy("Int32")

val floatTy: Ty = ConstTy("Float")
val doubleTy: Ty = ConstTy("Double")

val strTy = PtrTy(charTy)

data class Scheme(val names: Set<String>, val ty: Ty) {
  constructor(ty: Ty) : this(emptySet(), ty)

  override fun toString(): String = when {
    names.isEmpty() -> "$ty"
    else -> "∀ ${names.joinToString(" ") { "'$it" }}. $ty"
  }
}

infix fun Ty.arr(other: Ty): FunTy {
  return FunTy(this, other)
}

fun Ty.unapply(): List<Ty> = when (this) {
  is AppTy -> buildList {
    var ty: Ty = this@unapply
    while (ty is AppTy) {
      add(ty.arg)
      ty = ty.fn
    }
  }
  else -> emptyList()
}

fun Collection<TypeRef>.ty(): List<Ty> = map { it.ty() }

fun TypeRef.ty(): Ty = when (this) {
  is AccessTypeRef -> ConstTy(path.text)
  is GenericTypeRef -> VarTy(name.text)
  is FunctionTypeRef -> FunTy(parameterType.ty(), returnType.ty())
  is PointerTypeRef -> PtrTy(type.ty())
  is ApplyTypeRef -> arguments.fold(function.ty()) { acc, next -> AppTy(acc, next.ty()) }
  is UnitTypeRef -> unitTy
}

fun Ty.chainParameters(): List<Ty> = when (this) {
  is FunTy -> buildList {
    var ty: Ty = this@chainParameters
    while (ty is FunTy) {
      add(ty.parameterTy)
      ty = ty.returnTy
    }
  }
  else -> emptyList()
}

fun Ty.constructor(): Ty = when (this) {
  is AppTy -> this
  is ConstTy -> this
  else -> throw IsNotConstructor(this)
}

fun Ty.ungeneralize(): ConstTy = when (this) {
  is ConstTy -> this
  is AppTy -> fn.ungeneralize()
  else -> throw CanNotUngeneralize(this)
}

fun Ty.callable(): FunTy = when (this) {
  is FunTy -> this
  else -> throw IsNotCallable(this)
}

fun Ty.enumVariant(): Ty = when (this) {
  is AppTy -> this
  is ConstTy -> this
  is FunTy -> this
  else -> throw IsNotCallable(this)
}
