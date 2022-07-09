package org.plank.build

import java.io.File
import java.lang.System.getenv
import java.nio.file.Files
import java.nio.file.Paths

object LlvmConfig {
  val executable: File by lazy {
    LocalProperties.getOrNull("llvm.config")?.let(::File)
      ?: getenv("LLVM4K_CONFIG")?.let(::File)
      ?: locate()
  }

  fun hasLlvm(): Boolean {
    return runCatching { executable }.isSuccess
  }

  fun locate(): File {
    return getenv("PATH").split(File.pathSeparatorChar)
      .map { path ->
        if (path.startsWith("'") || path.startsWith("\"")) {
          path.substring(1, path.length - 1)
        } else {
          path
        }
      }
      .map(Paths::get)
      .firstOrNull { path -> Files.exists(path.resolve("llvm-config")) }
      ?.resolve("llvm-config")
      ?.toFile()
      ?: error("No suitable version of LLVM was found.")
  }

  fun cmd(vararg args: String): String {
    val command = "${executable.absolutePath} ${args.joinToString(" ")}"
    val process = Runtime.getRuntime().exec(command)
    val output = process.inputStream.bufferedReader().readText()

    val exitCode = process.waitFor()
    if (exitCode != 0) {
      error("Command `$command` failed with status code: $exitCode")
    }

    return output.replace("\n", "")
  }
}
