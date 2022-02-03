package org.plank.cli

import com.github.ajalt.clikt.core.ProgramResult
import org.plank.compiler.pkg.Package

fun Package.crash(message: String): Nothing {
  severe(message)
  throw ProgramResult(1)
}
