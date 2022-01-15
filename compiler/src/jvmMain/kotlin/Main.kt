package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.analyzer.analyze
import com.gabrielleeg1.plank.grammar.element.PlankFile
import org.llvm4j.llvm4j.Module
import java.io.File
import kotlin.io.path.createTempDirectory

private const val linker = "/home/gabi/Programs/swift-5.3.1-RELEASE-ubuntu20.04/usr/bin/clang++"

private fun compileStdlibFile(file: File, target: File): String {
  return listOf(
    linker,
    "-g",
    "-O3",
    "-c ${file.absolutePath}",
    "-o ${target.absolutePath}"
  ).joinToString(" ")
}

private fun linkCommand(file: File, target: File): String {
  return listOf(
    linker,
    "-c ${file.absolutePath}",
    "-o ${target.absolutePath}"
  ).joinToString(" ")
}

private fun compileCommand(files: List<String>, name: String): String {
  return listOf(
    linker,
    "-o $name",
    "-v ${files.joinToString(" ")}",
  ).joinToString(" ")
}

private fun runCompilation(module: Module) {
  println("[Plank] Running compilation")
  val tmpDir = createTempDirectory("compiler-test").toFile()

  fun exec(command: String) {
    println("[Plank] Executing command: $command")

    val process = Runtime.getRuntime().exec(command, null, tmpDir).apply {
      inputStream.bufferedReader().lineSequence().forEach {
        println(it)
      }

      errorStream.bufferedReader().lineSequence().forEach {
        println(it)
      }
    }

    val exitCode = process.waitFor()
    if (exitCode != 0) {
      error("Command $command failed with exit code $exitCode")
    }
  }

  val tmpRuntimeDir = tmpDir.resolve("runtime")
  val runtimeDir = File("runtime")
  runtimeDir.copyRecursively(tmpRuntimeDir)

  println("[Plank] Compiling stdlib")
  val stdObjects = tmpRuntimeDir.listFiles().orEmpty().map { stdFile ->
    val objectFile = tmpRuntimeDir.resolve("${stdFile.nameWithoutExtension}.o")
    exec(compileStdlibFile(stdFile, objectFile))
    objectFile
  }

  val binaryFile = tmpDir.resolve("binary")

  println("[Plank] Compiling program into object")
  val objectFiles = stdObjects + run {
    val llvmFile = tmpDir.resolve("compilation.ll").apply {
      createNewFile()
      writeText(module.getAsString())
    }
    val objectFile = tmpDir.resolve("compilation.o")

    exec(linkCommand(llvmFile, objectFile))

    objectFile
  }
  println("[Plank] Objects generated")
  objectFiles.forEach {
    println("  [Plank] ${it.absolutePath}")
  }

  println("[Plank] Compiling program object")
  exec(compileCommand(objectFiles.map { it.absolutePath }, binaryFile.absolutePath))

  println("[Plank] Running program")
  exec(binaryFile.absolutePath)
}

fun main() {
  val file = PlankFile.of(
    """
    native fun println(message: *Char): Void

    fun main(argc: Int32, argv: **Char): Void {
      println("Hello, world");
    }
    """.trimIndent(),
  )

  val results = compile(file, ::analyze).orNull() ?: error("Compilation failed")

  println("LLVM Module:")
  println(results.getAsString())
  println("==============")

  runCompilation(results)
}
