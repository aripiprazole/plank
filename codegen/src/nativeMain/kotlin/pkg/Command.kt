package org.plank.codegen.pkg

import pw.binom.Environment
import pw.binom.getEnv
import pw.binom.io.file.File
import pw.binom.io.file.isExist

expect val pathSeparator: String

expect fun Command.exec(): String

data class Command(val executable: File, private val args: MutableList<String> = mutableListOf()) {
  fun arg(arg: String): Command {
    args.add(arg)
    return this
  }

  override fun toString(): String {
    return "$executable ${args.joinToString(" ")}"
  }

  companion object {
    fun of(executable: File): Command {
      return Command(executable)
    }

    fun of(executable: String): Command {
      return Command(locateBinary(executable))
    }
  }
}

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class CommandFailedException(val command: String, val exitCode: Int) : RuntimeException() {
  override val message: String = "Command $command failed with exit code $exitCode"
}

fun locateBinary(name: String): File {
  return Environment.getEnv("PATH")!!
    .split(pathSeparator)
    .map { path ->
      if (path.startsWith("'") || path.startsWith("\"")) {
        path.substring(1, path.length - 1)
      } else {
        path
      }
    }
    .map { File(it) }
    .firstOrNull { directory -> File(directory, name).isExist }
    ?.let { File(it, name) }
    ?: error("Could not find `$name` in PATH")
}
