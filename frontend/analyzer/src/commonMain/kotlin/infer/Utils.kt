package org.plank.analyzer.infer

inline fun <A, B, C> Pair<A, B>.mapFirst(f: (A) -> C): Pair<C, B> = Pair(f(first), second)

inline fun <A, B, C> Pair<A, B>.mapSecond(f: (B) -> C): Pair<A, C> = Pair(first, f(second))
