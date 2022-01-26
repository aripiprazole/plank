package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.element.ResolvedFunDecl
import com.gabrielleeg1.plank.grammar.element.QualifiedPath
import java.math.BigInteger
import java.security.MessageDigest

private fun sha256(identifier: String): String {
  val sha256 = MessageDigest.getInstance("SHA-256")
  sha256.reset()
  sha256.update(identifier.toByteArray())
  return "%064x".format(BigInteger(1, sha256.digest()))
}

fun CompilerContext.mangleFunction(function: ResolvedFunDecl): String {
  val module = QualifiedPath(name)

  val name = buildString {
    append("_Z")
    module.fullPath.reversed().forEach { (name) ->
      append(name.length)
      append(name)
    }
    append(function.name.text.length)
    append(function.name.text)
  }

  return name + 5 + sha256(name).substring(0, 5)
}
