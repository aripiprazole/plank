package com.lorenzoog.plank.build

object Dependencies {
  object Kotlin {
    const val Coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2"
    const val CoroutinesJdk8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.2"
  }

  object Pinterest {
    const val Ktlint = "com.pinterest:ktlint:0.40.0"
  }

  object Eclipse4J {
    const val Eclipse4J = "org.eclipse.lsp4j:org.eclipse.lsp4j:0.10.0"
    const val JsonRPC = "org.eclipse.lsp4j:org.eclipse.lsp4j.jsonrpc:0.10.0"
  }

  object Binom {
    const val File = "pw.binom.io:file:0.1.19"
  }

  object Antlr {
    const val Antlr4 = "org.antlr:antlr4:4.7.1"
    const val AntlrKotlinTarget = "com.strumenta.antlr-kotlin:antlr-kotlin-target:0ad2c42952"
  }

  object LLVM4J {
    const val LLVM4J = "org.llvm4j:llvm4j:0.1.0-SNAPSHOT"
  }

  object ByteDeco {
    const val LLVMPlatform = "org.bytedeco:llvm-platform:10.0.1-1.5.4"
  }

  object Jansi {
    const val Jansi = "org.fusesource.jansi:jansi:2.0.1"
  }

  object Clikt {
    const val Clikt = "com.github.ajalt.clikt:clikt:3.1.0"
  }
}
