package com.gabrielleeg1.plank.compiler.compile

import java.io.File

fun Process.printOutput(): Process = apply {
  inputStream.bufferedReader().lineSequence().forEach {
    println(it)
  }

  errorStream.bufferedReader().lineSequence().forEach {
    println(it)
  }
}

fun compileStdlibFile(linker: String, file: File, target: File): String {
  return listOf(
    linker,
    "-g",
    "-O3",
    "-c ${file.absolutePath}",
    "-o ${target.absolutePath}"
  ).joinToString(" ")
}

fun linkCommand(linker: String, file: File, target: File): String {
  return listOf(
    linker,
    "-c ${file.absolutePath}",
    "-o ${target.absolutePath}"
  ).joinToString(" ")
}

fun compileCommand(linker: String, files: List<String>, name: String): String {
  return listOf(
    linker,
    "-o $name",
    "-v ${files.joinToString(" ")}",
  ).joinToString(" ")
}
