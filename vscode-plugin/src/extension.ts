// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import * as vscode from 'vscode';
import * as path from 'path';

import {LanguageClient, LanguageClientOptions, ServerOptions, TransportKind} from 'vscode-languageclient/node';

let client: LanguageClient;

const MAIN_CLASS = 'com.gabrielleeg1.jplank.tooling.langserver.MainKt';
const JAVA_HOME = process.env.JAVA_HOME!!;

const JAVA = path.join(JAVA_HOME, 'bin', 'java');
const JAR_PATH = path.join(
  '..', 'lang-server', 'build', 'libs', 'lang-server-1.0-SNAPSHOT-all.jar'
);


// this method is called when your extension is activated
// your extension is activated the very first time the command is executed
export function activate(context: vscode.ExtensionContext) {
  const serverOptions: ServerOptions = {
    command: JAVA,
    args: ['-cp', context.asAbsolutePath(JAR_PATH), MAIN_CLASS],
    options: {},
    transport: TransportKind.stdio
  };

  const clientOptions: LanguageClientOptions = {
    documentSelector: [{scheme: 'file', language: 'plaintext'}],
    synchronize: {
      fileEvents: vscode.workspace.createFileSystemWatcher('**/*.plank')
    }
  };

  client = new LanguageClient('plankls', serverOptions, clientOptions);
  client.start();
}

// this method is called when your extension is deactivated
export async function deactivate() {
  await client.stop()
}
