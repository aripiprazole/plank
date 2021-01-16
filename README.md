# PLANK

![GitHub Repo stars](https://img.shields.io/github/stars/LorenzooG/jplank?color=orange&style=for-the-badge)
![GitHub issues](https://img.shields.io/github/issues/LorenzooG/jplank?color=orange&style=for-the-badge)
![GitHub last commit](https://img.shields.io/github/last-commit/LorenzooG/jplank?color=orange&style=for-the-badge)

Plank is a simple language made with LLVM and ANTLR in Kotlin. Need help? contact me
on [twitter](https://twitter.com/lorenzoo_g) or message me on discord **LorenzooG#9722**.

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
import io;

fun main(argc: Int, argv: *String) {
  println("Hello, world");
}
```

You can find more examples [here](samples)

## Modules

| Name                                 | Description                                                          |
| ------------------------------------ | -------------------------------------------------------------------- |
| [cli](cli)                           | Holds all command-line stuff                                         | 
| [grammar](grammar)                   | Implements ANTLR/grammar stuff                                       |
| [composite-build](composite-build)   | Implements the build plugin with dependencies, version, etc          |
| [compiler](compiler)                 | Implements the LLVM core that compiles to IR representation          |
| [runtime](runtime)                   | Implements the engine runtime functions                              |
| [stdlib](stdlib)                     | Implements the language stdlib                                       |
| [intellij-plugin](stdlib)            | Implements the intellij tooling plugin                               |

## Dependencies

| Name       | Link                              | Version    |
| ---------- | --------------------------------- | ---------- |
| kotlin     | https://kotlinlang.org            | v1.4.30-M1 |
| java       | https://openjdk.java.net/         | v11        |
| clang++    | https://clang.llvm.org            | v10.0.0    |
| cmake      | https://cmake.org                 | v3.16      |
| make       | https://www.gnu.org/software/make | v4.3       |

## CLI

You should define the environment variable `PLANK_HOME` to
the project folder.

```
Usage: plank [OPTIONS] [target]...

Options:
  -o, --output TEXT              The output name
  --emit-llvm                    Emits the LLVM IR and exit
  -v, --verbose                  Enables the debug mode
  --cmake TEXT                   The path to cmake bin
  --make TEXT                    The path to make binary
  -l, --linker TEXT              The path to linker binary
  -s, --src TEXT                 The path to src dir
  -cmbd, --cmake-build-dir TEXT  The path to compile stdlib
  -od, --objects-dir TEXT        The path to compile stdlib
  --bytecode-dir TEXT            The path to emit bytecode
  -bd, --bin-dir TEXT            The path to binaries
  -dd, --dist-dir TEXT           The path to build
  -h, --help                     Show this message and exit

Arguments:
  target  The target files that will be compiled
```

## Building

You should execute the following commands:

```shell
$ git clone git@github.com:LorenzooG/jplank.git
$ cd jplank
# if you are in windows
# $ gradlew.bat cli:shadowJar
$ ./gradlew cli:shadowJar
```

The jar is located in `cli/build/libs`

## Pull Requests

PRs are welcome as long as they are well explained and only change one feature at a time.  
This doesn't mean i'm gonna accept all PRs, it just means i'm gonna consider it.

## Ending

If you liked and/or use the language consider leaving a star on the repo and following me on twitter :)

