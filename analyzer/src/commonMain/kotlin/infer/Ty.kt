package org.plank.analyzer.infer

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

data class FunTy(val returnTy: Ty, val parameterTy: Ty) : Ty {
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

  fun chainParameters(): List<Ty> = buildList {
    var ty: Ty = this@FunTy
    while (ty is FunTy) {
      add(ty.parameterTy)
      ty = ty.returnTy
    }
  }
}

fun FunTy(returnTy: Ty, parameters: Collection<Ty>): FunTy =
  parameters
    .reversed()
    .ifEmpty { listOf(unitTy) }
    .fold(returnTy) { acc, ty -> FunTy(acc, ty) } as FunTy

data class AppTy(val fn: Ty, val arg: Ty) : Ty {
  fun unapply(): List<Ty> = buildList {
    var ty: Ty = this@AppTy
    while (ty is AppTy) {
      add(ty.arg)
      ty = ty.fn
    }
  }
  override fun toString(): String = "$fn $arg"
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

data class Scheme(val names: Set<String>, val ty: Ty) {
  constructor(ty: Ty) : this(ty.ftv().sorted().toSet(), ty)

  override fun toString(): String = when {
    names.isEmpty() -> ty.toString()
    else -> "∀ ${names.joinToString(" ") { "'$it" }}. $ty"
  }
}
