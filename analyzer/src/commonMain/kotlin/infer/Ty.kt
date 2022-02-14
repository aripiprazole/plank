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

val charTy: Ty = ConstTy("Int8")

val i8Ty: Ty = ConstTy("Int8")
val i16Ty: Ty = ConstTy("Int16")
val i32Ty: Ty = ConstTy("Int32")

val floatTy: Ty = ConstTy("Float")
val doubleTy: Ty = ConstTy("Double")

fun pointer(type: Ty): AppTy = AppTy(ConstTy("Pointer"), type)

data class Scheme(val names: Set<String>, val type: Ty) {
  override fun toString(): String = "∀ ${names.joinToString(" ")}. $type"
}
