package com.gabrielleeg1.plank.cli

import com.gabrielleeg1.plank.cli.commands.Plank
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
fun main(args: Array<String>) {
  Plank().main(args)
}
