package com.lorenzoog.jplank.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.lorenzoog.jplank.analyzer.DefaultBindingContext
import com.lorenzoog.jplank.compiler.PlankCompiler
import com.lorenzoog.jplank.compiler.PlankLLVM
import com.lorenzoog.jplank.linker.LinkerOpts
import com.lorenzoog.jplank.linker.PlankLinker
import com.lorenzoog.jplank.message.ColoredMessageRenderer
import com.lorenzoog.jplank.stdlib.Stdlib
import pw.binom.io.file.File
import pw.binom.io.file.mkdirs
import java.nio.file.Paths
import kotlin.system.exitProcess

class Plank : CliktCommand() {
  private val stdlib = Stdlib()
  private val stdlibFiles = stdlib.readStdlib()

  private val context = DefaultBindingContext(stdlibFiles)
  private val renderer = ColoredMessageRenderer(flush = true)
  private val compiler = PlankLLVM(stdlibFiles, context)

  private val target by argument("target")
    .help("The target files that will be compiled").multiple()

  private val outputName by option("-o", "--output")
    .help("The output name").default("main")

  private val emitLLVM by option("--emit-llvm")
    .help("Emits the LLVM IR and exit").flag()

  private val debug by option("-v", "--verbose")
    .help("Enables the debug mode").flag()

  private val cmakePath by option("--cmake")
    .help("The path to cmake bin").default("/usr/bin/cmake")

  private val makePath by option("--make")
    .help("The path to make binary").default("/usr/bin/make")

  private val linkerPath by option("-l", "--linker")
    .help("The path to linker binary").default("/usr/bin/clang++")

  private val srcDirPath by option("-s", "--src")
    .help("The path to src dir").default(Paths.get("").toAbsolutePath().toString())

  private val cmakeBuildDirPath by option("-cmbd", "--cmake-build-dir")
    .help("The path to compile stdlib").default("dist/cmake")

  private val objectsDirPath by option("-od", "--objects-dir")
    .help("The path to compile stdlib").default("dist/objects")

  private val bytecodeDirPath by option("--bytecode-dir")
    .help("The path to emit bytecode").default("dist/bytecode")

  private val binDirPath by option("-bd", "--bin-dir")
    .help("The path to binaries").default("bin")

  private val buildDirPath by option("-dd", "--dist-dir")
    .help("The path to build").default("dist")

  override fun run() {
    val srcDir = File(srcDirPath)
    val binDir = File(binDirPath).also { it.mkdirs() }
    val buildDir = File(buildDirPath).also { it.mkdirs() }
    val objectsDir = File(objectsDirPath).also { it.mkdirs() }
    val cmakeBuildDir = File(cmakeBuildDirPath).also { it.mkdirs() }
    val bytecodeDir = File(bytecodeDirPath).also { it.mkdirs() }

    buildDir.delete()
    buildDir.mkdir()

    val opts = LinkerOpts(srcDir).apply {
      linkerPath = this@Plank.linkerPath
      cmakePath = this@Plank.cmakePath
      makePath = this@Plank.makePath
      debug = this@Plank.debug
      this.bytecodeDir = bytecodeDir
      this.buildDir = buildDir
      this.binDir = binDir
      this.objectsDir = objectsDir
      this.cmakeBuildDir = cmakeBuildDir
    }

    val linker = PlankLinker(opts, renderer)
    val compiler = PlankCompiler(linker, context, compiler, renderer)
    val src = target.map { File(srcDir, it) }

    if (stdlib.path == null) {
      return renderer.severe("Define the PLANK_HOME before compile")
    }

    if (!compiler.generateIR(src)) {
      renderer.severe("Aborting")
      exitProcess(1)
    }

    if (emitLLVM) {
      renderer.info("Issued llvm ir of ${target.joinToString(prefix = "[", postfix = "]")}")
    }

    if (!compiler.generateBinary(outputName)) {
      renderer.severe("Aborting")
      exitProcess(1)
    }

    renderer.info("Successfully linked $outputName")
  }
}
