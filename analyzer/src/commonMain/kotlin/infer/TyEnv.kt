package org.plank.analyzer.infer

import kotlin.jvm.JvmInline

@JvmInline
value class TyEnv(val map: Map<String, Scheme>) {
  fun lookup(name: String): Scheme? = map[name]

  fun extend(name: String, scheme: Scheme): TyEnv = TyEnv(map = map + (name to scheme))

  operator fun plus(other: TyEnv): TyEnv = TyEnv(map = map + other.map)

  override fun toString(): String = "TyEnv $map"
}

fun TyEnv(builder: MutableMap<String, Scheme>.() -> Unit): TyEnv =
  TyEnv(mutableMapOf<String, Scheme>().apply(builder))

fun nullEnv(): TyEnv = TyEnv(mutableMapOf())
