package com.gabrielleeg1.plank.compiler.compile

import kotlin.io.path.createTempDirectory

private fun execBinary(code: String): Int {
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
    error.violations.forEach { it.render(pkg.logger) }
  } catch (error: SyntaxError) {
    error.violations.forEach { it.render(pkg.logger) }
  }
  return -1
}

fun main() {
  val code = """native fun println(message: *Char): Void

type Person = {mutable name: *Char};

fun person(): *Person {
  let p = Person{name: "Gabrielle"};
  return &p;
}

fun main(argc: Int32, argv: **Char): Void {
  let person = *person();
  println(person.name);
  person.name := "Alberto";
  println(person.name);
}
"""

  execBinary(code)
}
