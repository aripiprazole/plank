package org.plank.tooling.langserver

import org.eclipse.lsp4j.CompletionOptions
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult
import org.eclipse.lsp4j.ServerCapabilities
import org.eclipse.lsp4j.TextDocumentSyncKind
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.WorkspaceService
import pw.binom.io.Closeable
import java.util.concurrent.CompletableFuture

class PlankLanguageServer : LanguageServer, LanguageClientAware, Closeable {
  private lateinit var client: LanguageClient
  private val textDocumentService = PlankTextDocumentService()
  private val workspaceService = PlankWorkspaceService()

  override fun connect(client: LanguageClient) {
    this.client = client
  }

  override fun initialize(params: InitializeParams): CompletableFuture<InitializeResult> {
    val capabilities = ServerCapabilities().apply {
      completionProvider = CompletionOptions()
      textDocumentSync = Either.forLeft(TextDocumentSyncKind.Full)
    }

    return CompletableFuture.completedFuture(
      InitializeResult(capabilities)
    )
  }

  override fun shutdown(): CompletableFuture<Any> {
    close()
    return CompletableFuture.completedFuture(null)
  }

  override fun getTextDocumentService(): TextDocumentService {
    return textDocumentService
  }

  override fun getWorkspaceService(): WorkspaceService {
    return workspaceService
  }

  override fun close() {}

  override fun exit() {}
}
