package com.gabrielleeg1.plank.compiler.compile

import com.gabrielleeg1.plank.grammar.element.PlankFile
import com.gabrielleeg1.plank.grammar.tree.toParseTree
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

fun main() {
  val code = """
@external("PLANK_INTERNAL_println")
fun println(message: *Char): Void

type Person = {mutable name: *Char};

fun create_gabrielle(): *Person {
  return &Person{name: "Gabrielle"};
}

fun create_alfredo(): *Person {
  return &Person{name: "Alfredo"};
}

fun create_gerson(): Person {
  return Person{name: "Gerson"};
}

fun main(argc: Int32, argv: **Char): Void {
  println(create_gerson().name);
  let mutable person = *create_gabrielle();
  println(person.name);
  person := *create_alfredo();
  println(person.name);
  person.name := "Alberto";
  println(person.name);
}
"""

  PlankFile
    .parser(code).file()
    .toParseTree()
    .multilineString()
    .also(::println)

  execBinary(code)
}
