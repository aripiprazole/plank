package org.plank.tooling.langserver

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.CompletionItemKind
import org.eclipse.lsp4j.CompletionList
import org.eclipse.lsp4j.CompletionParams
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.TextDocumentService

class PlankTextDocumentService : TextDocumentService {
  private val scope = CoroutineScope(Dispatchers.IO)
  private var document = ""

  override fun completion(position: CompletionParams) = scope.future {
    val list = CompletionList().apply {
      items = mutableListOf(
        CompletionItem().apply {
          label = "hello()"
          kind = CompletionItemKind.Snippet
          tags = mutableListOf()
          insertText = """
            fun hello(): Void {
              println("Hello, world!");
            }
          """.trimIndent()
        },
      )
      setIsIncomplete(false)
    }

    Either.forRight<List<CompletionItem>, CompletionList>(list)
  }

  override fun didOpen(params: DidOpenTextDocumentParams) {
    document = params.textDocument.text
  }

  override fun didChange(params: DidChangeTextDocumentParams) {
    TODO("Not yet implemented")
  }

  override fun didClose(params: DidCloseTextDocumentParams) {
    TODO("Not yet implemented")
  }

  override fun didSave(params: DidSaveTextDocumentParams) {
    TODO("Not yet implemented")
  }
}
