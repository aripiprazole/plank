# PLANK

![GitHub Repo stars](https://img.shields.io/github/stars/plank-lang/plank?color=orange&style=for-the-badge)
![GitHub issues](https://img.shields.io/github/issues/plank-lang/plank?color=orange&style=for-the-badge)
![GitHub last commit](https://img.shields.io/github/last-commit/plank-lang/plank?color=orange&style=for-the-badge)

Plank is a simple language made with LLVM and ANTLR in Kotlin. Need help? contact me
on [Twitter](https://twitter.com/gabrielleeg1) or message me on discord **gabii#9722**.

### Content

* [Example](#example)
* [Modules](#modules)
* [Dependencies](#dependencies)
* [CLI](#cli)
* [Building](#building)
* Documentation (WIP)
* [Pull Requests](#pull-requests)
* [Ending](#ending)

## Example

Hello world in plank:

```kotlin
import Std.IO;

fun main(argc: Int32, argv: **Char): Void {
  println("Hello, world");
}
```

You can find more examples [here](samples)

## Modules

| Name                      | Description                                      |
|---------------------------|--------------------------------------------------|
| [cli](cli)                | All command-line stuff                           | 
| [grammar](grammar)        | AST and Descriptor Mapping                       |
| [parser](parser)          | ANTLR grammar                                    |
| [compiler](compiler)      | The LLVM core that compiles to IR representation |
| [analyzer](runtime)       | Code analyzing                                   |
| [runtime](runtime)        | Runtime functions                                |
| [stdlib](stdlib)          | Language stdlib                                  |
| [intellij-plugin](stdlib) | Intellij tooling plugin                          |

## CLI

```
Usage: plank [OPTIONS] COMMAND [ARGS]...

Options:
  -h, --help  Show this message and exit

Commands:
  jit
  repl
```

## Building

The LLVM 13.0.0 is required, you can either put `llvm-config` in the `$PATH` environment variable, either create a
`local.properties` and put `llvm.config=<target to llvm-config executable>` in it.

You should execute the following commands:

```shell
$ git clone git@github.com:plank-lang/plank.git
$ cd plank
# if you are in windows
# PS .\gradlew.bat cli:linkPlankReleaseExecutableMingwX64
$ ./gradlew cli:linkPlankReleaseExecutableLinuxX64
```

The binary file `cli/build/bin/linuxX64/plankReleaseExecutable` or if you are in a Windows
machine `cli/build/bin/mingwX64/plankReleaseExecutable` will be created.

## Install

This is the [linux install instructions](#Install Linux) that is tested in ubuntu; if you want the distributions for
your operational system, you must follow the
[Build instructions](#Building).

## Install Linux

You must download a release in [releases](https://github.com/plank-lang/plank/releases/tag/1.0.1-SNAPSHOT)
or build your one in [Building](#Building). After that you must define the environment variable `PLANK_HOME`
to the unzipped release folder, and set `PATH` to `$PLANK_HOME/bin`. So the plank is installed, make good use. 🙂

## Pull Requests

PRs are welcome as long as they are well explained and only change one feature at a time.  
This doesn't mean I'm going to accept all PRs, it just means I'm going to consider it.

## Ending

If you liked and/or use the language consider leaving a star on the repo and following me on twitter :)

