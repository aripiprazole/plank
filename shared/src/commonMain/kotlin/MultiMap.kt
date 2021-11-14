package com.gabrielleeg1.plank.shared

class MultiMap<K, V>(
  map: MutableMap<K, MutableList<V>> = mutableMapOf()
) : MutableMap<K, MutableList<V>> by map {
  operator fun set(key: K, value: V): List<V> {
    return put(key, value)
  }

  private fun put(key: K, value: V): List<V> {
    val list = getOrPut(key) { mutableListOf() }
    list += value
    return list
  }
}
