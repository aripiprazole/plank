package org.plank.analyzer.infer

sealed interface Ty {
  override fun toString(): String
}

data class ConstTy(val name: String) : Ty {
  override fun toString(): String = name
}

data class VarTy(val name: String) : Ty {
  override fun toString(): String = name
}

data class AppTy(val fn: Ty, val arg: Ty) : Ty {
  override fun toString(): String {
    val arg = if (arg is AppTy && arg.fn is ConstTy && arg.fn.name == "->") {
      "($arg)"
    } else {
      "$arg"
    }

    return when {
      fn is ConstTy && fn.name == "->" -> "$arg ->"
      else -> "$fn $arg"
    }
  }
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

fun pointerTy(type: Ty): AppTy = AppTy(ConstTy("*"), type)

fun arrayTy(type: Ty): AppTy = AppTy(ConstTy("arr"), type)

fun arrowTy(returnTy: Ty, parameter: Ty): Ty = arrowTy(returnTy, listOf(parameter))

fun arrowTy(returnTy: Ty, parameters: Collection<Ty>): Ty =
  parameters
    .ifEmpty { listOf(unitTy) }
    .fold(returnTy) { acc, ty -> AppTy(AppTy(ConstTy("->"), ty), acc) }

fun AppTy.chainArgs(): List<Ty> = buildList {
  var ty: Ty = this@chainArgs
  while (ty is AppTy) {
    add(ty.arg)
    ty = ty.fn
  }
  add(ty)
}.reversed()

fun Ty.unapply(): Ty? = (this as? AppTy)?.arg

fun Ty.isArrow(name: String = "->"): Boolean =
  this is AppTy && ((fn is ConstTy && fn.name == name) || fn.isArrow(name))

data class Scheme(val names: Set<String>, val type: Ty) {
  override fun toString(): String = "∀ ${names.joinToString(" ")}. $type"
}
