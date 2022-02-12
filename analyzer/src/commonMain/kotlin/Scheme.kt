package org.plank.analyzer

sealed interface Mono

data class MConst(val name: String) : Mono {
  override fun toString(): String = name
}

data class MVar(val name: String) : Mono {
  override fun toString(): String = name
}

data class MApp(val fn: Mono, val arg: Mono) : Mono {
  override fun toString(): String {
    val arg = if (arg is MApp && arg.fn is MConst && arg.fn.name == "->") {
      "($arg)"
    } else {
      "$arg"
    }

    return when {
      fn is MConst && fn.name == "->" -> "$arg ->"
      else -> "$fn $arg"
    }
  }
}

object MUndef : Mono by MConst("!")
object MUnit : Mono by MConst("()")
object MBool : Mono by MConst("Bool")
object MInt32 : Mono by MConst("Int32")

fun pointer(type: Mono): MApp = MApp(MConst("Pointer"), type)

data class Scheme(val names: Set<String>, val type: Mono) {
  override fun toString(): String = "∀ ${names.joinToString(" ")}. $type"
}
