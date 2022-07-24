package org.plank.codegen.pkg

import okio.Path
import okio.Path.Companion.toPath
import org.plank.shared.Environment
import org.plank.shared.Platform

data class Command(val executable: Path, private val args: MutableList<String> = mutableListOf()) {
  fun arg(arg: String): Command {
    args.add(arg)
    return this
  }

  override fun toString(): String {
    return "$executable ${args.joinToString(" ")}"
  }

  companion object {
    fun of(executable: Path): Command {
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

fun locateBinary(name: String): Path {
  return Environment["PATH"]!!
    .split(pathSeparator)
    .map { path ->
      if (path.startsWith("'") || path.startsWith("\"")) {
        path.substring(1, path.length - 1)
      } else {
        path
      }
    }
    .map { it.toPath() }
    .firstOrNull { directory -> Platform.FileSystem.exists(directory.resolve(name)) }
    ?.resolve(name)
    ?: error("Could not find `$name` in PATH")
}
