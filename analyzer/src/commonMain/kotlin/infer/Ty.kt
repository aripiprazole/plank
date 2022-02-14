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

object UndefTy : Ty by ConstTy("!")
object UnitTy : Ty by ConstTy("()")
object BoolTy : Ty by ConstTy("Bool")
object Int32Ty : Ty by ConstTy("Int32")

fun pointer(type: Ty): AppTy = AppTy(ConstTy("Pointer"), type)

data class Scheme(val names: Set<String>, val type: Ty) {
  override fun toString(): String = "∀ ${names.joinToString(" ")}. $type"
}
