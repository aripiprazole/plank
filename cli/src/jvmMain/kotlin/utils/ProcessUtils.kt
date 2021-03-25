package com.lorenzoog.jplank.utils

fun Process.printOutput() {
  inputStream.bufferedReader().lineSequence().forEach {
    println(it)
  }

  errorStream.bufferedReader().lineSequence().forEach {
    println(it)
  }
}
