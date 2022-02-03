package org.plank.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class PlankCLI : CliktCommand(name = "plank") {
  init {
    subcommands(PlankJIT(), PlankREPL())
  }

  override fun run() {
    if (currentContext.invokedSubcommand != null) return

    TODO("Not yet implemented: plank")
  }
}
