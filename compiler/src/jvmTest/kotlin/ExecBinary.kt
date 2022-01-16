package com.gabrielleeg1.plank.compiler

import com.gabrielleeg1.plank.compiler.compile.BindingError
import com.gabrielleeg1.plank.compiler.compile.FailedCommand
import com.gabrielleeg1.plank.compiler.compile.IRDumpError
import com.gabrielleeg1.plank.compiler.compile.Package
import com.gabrielleeg1.plank.compiler.compile.SyntaxError
import com.gabrielleeg1.plank.compiler.compile.compileBinary
import com.gabrielleeg1.plank.compiler.compile.printOutput
import kotlin.io.path.createTempDirectory

fun execBinary(code: String): Int {
  val pkg = Package(code) {
    linker =
      "/home/gabi/Programs/swift-5.3.1-RELEASE-ubuntu20.04/usr/bin/clang++" // todo change linker
    output = dist.resolve("main")
    dist = createTempDirectory("plank-test").toFile()
    debug = true
  }

  try {
    val binary = pkg.compileBinary()

    return Runtime.getRuntime().exec(binary.absolutePath).printOutput().waitFor()
  } catch (error: BindingError) {
    pkg.logger.severe("BindingError")
    error.violations.forEach { it.render(pkg.logger) }
  } catch (error: SyntaxError) {
    pkg.logger.severe("SyntaxError")
    error.violations.forEach { it.render(pkg.logger) }
  } catch (error: IRDumpError) {
    pkg.logger.severe("IRDumpError")
    error.violations.forEach {
      pkg.logger.severe(it.render())
    }
    error.printStackTrace()
  } catch (error: FailedCommand) {
    error.printStackTrace()
    return error.exitCode
  }
  return -1
}
