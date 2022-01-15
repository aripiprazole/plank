package com.gabrielleeg1.plank.compiler.compile

import kotlin.io.path.createTempDirectory

private fun execBinary(code: String): Int {
  val pkg = Package(code) {
    linker = "/home/gabi/Programs/swift-5.3.1-RELEASE-ubuntu20.04/usr/bin/clang++" // todo change linker
    output = dist.resolve("main")
    dist = createTempDirectory("plank-test").toFile()
    debug = true
  }

  val binary = pkg.compileBinary()

  return Runtime.getRuntime().exec(binary.absolutePath).printOutput().waitFor()
}

fun main() {
  execBinary(
    """
    native fun println(message: *Char): Void

    fun main(argc: Int32, argv: **Char): Void {
      println("Hello, world");
    }
    """.trimIndent(),
  )
}
