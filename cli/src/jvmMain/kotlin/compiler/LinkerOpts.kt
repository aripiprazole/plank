package com.lorenzoog.jplank.compiler

import pw.binom.io.file.File
import pw.binom.io.file.asJFile

class LinkerOpts(private val srcDir: File) {
  var linkerPath = "/usr/bin/clang++"
  var cmakePath = "/usr/bin/cmake"
  var makePath = "/usr/bin/make"
  var debug = false
  var binDir = File(srcDir, "bin")
  var buildDir = File(srcDir, "dist")
  var objectsDir = File(buildDir, "objects")
  var bytecodeDir = File(buildDir, "bytecode")
  var cmakeBuildDir = File(buildDir, "cmake")

  internal fun buildCMakeCommand(): String {
    val runtimeDir = File(srcDir, "runtime")
    val buildType = if (debug) "Debug" else "Release"

    return cmakePath +
      " -S ${runtimeDir.asJFile.absolutePath}" +
      " -B ${cmakeBuildDir.asJFile.absolutePath}" +
      " -DTARGET_OBJECTS_DIR=${objectsDir.asJFile.absolutePath}" +
      " -DCMAKE_BUILD_TYPE=$buildType"
  }

  internal fun buildLLCCommand(file: File, target: File): String {
    return "$linkerPath -c ${file.path} -o ${target.path}"
  }

  internal fun buildLinkCommand(files: List<String>, name: String): String {
    return "$linkerPath -o $binDir/$name -v ${files.joinToString(" ")}"
  }
}
