package org.plank.codegen.pkg

import java.io.File
import org.plank.shared.Environment

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
class CommandFailedException(val command: String, val exitCode: Int, val output: String) :
  RuntimeException() {
  override val message: String =
    "Command $command failed with exit code $exitCode with output: $output"
}

fun locateBinary(name: String): File {
  return Environment["PATH"]!!
    .split(pathSeparator)
    .map { path ->
      if (path.startsWith("'") || path.startsWith("\"")) {
        path.substring(1, path.length - 1)
      } else {
        path
      }
    }
    .map { File(it) }
    .firstOrNull { directory -> directory.resolve(name).exists() }
    ?.resolve(name)
    ?: error("Could not find `$name` in PATH")
}
