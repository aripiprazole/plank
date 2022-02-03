package org.plank.cli

import com.github.ajalt.clikt.core.ProgramResult
import org.plank.codegen.pkg.Package

fun Package.crash(message: String): Nothing {
  severe(message)
  throw ProgramResult(1)
}
