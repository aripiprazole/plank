package com.lorenzoog.jplank.utils

object ProcessUtils {
  fun printOutput(process: Process) {
    process.inputStream.bufferedReader().lineSequence().forEach {
      println(it)
    }

    process.errorStream.bufferedReader().lineSequence().forEach {
      println(it)
    }
  }
}
