package com.lorenzoog.plank.tooling.langserver

import java.util.concurrent.Executors
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.services.LanguageClient

fun main() {
  val server = PlankLanguageServer()
  val launcher = Launcher.Builder<LanguageClient>()
    .setRemoteInterface(LanguageClient::class.java)
    .setLocalService(server)
    .wrapMessages { it }
    .setInput(System.`in`)
    .setOutput(System.out)
    .setExecutorService(Executors.newSingleThreadExecutor())
    .create()

  server.connect(launcher.remoteProxy)

  launcher.startListening()
}
