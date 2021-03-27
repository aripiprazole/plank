package com.lorenzoog.plank.cli

import com.lorenzoog.plank.cli.commands.Plank
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
fun main(args: Array<String>) {
  Plank().main(args)
}
