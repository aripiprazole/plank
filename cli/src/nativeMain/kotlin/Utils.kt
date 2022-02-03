package com.gabrielleeg1.plank.cli

import com.gabrielleeg1.plank.compiler.pkg.Package
import com.github.ajalt.clikt.core.ProgramResult

fun Package.crash(message: String): Nothing {
  severe(message)
  throw ProgramResult(1)
}
