import org.jetbrains.kotlin.gradle.testing.KotlinTaskTestRun
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties

val localProperties: Properties = rootProject.file("local.properties").let { file ->
  val properties = Properties()

  if (file.exists()) {
    properties.load(file.inputStream())
  }

  properties
}

fun locateLlvmConfig(): File {
  return System.getenv("PATH").split(File.pathSeparatorChar)
    .map { path ->
      if (path.startsWith("'") || path.startsWith("\"")) {
        path.substring(1, path.length - 1)
      } else {
        path
      }
    }
    .map(Paths::get)
    .firstOrNull { path -> Files.exists(path.resolve("llvm-config")) }
    ?.resolve("llvm-config")
    ?.toFile()
    ?: error("No suitable version of LLVM was found.")
}

val llvmConfig = localProperties.getProperty("llvm.config")?.let(::File)
  ?: System.getenv("LLVM4K_CONFIG")?.let(::File)
  ?: locateLlvmConfig()

fun cmd(vararg args: String): String {
  val command = "${llvmConfig.absolutePath} ${args.joinToString(" ")}"
  val process = Runtime.getRuntime().exec(command)
  val output = process.inputStream.bufferedReader().readText()

  val exitCode = process.waitFor()
  if (exitCode != 0) {
    error("Command `$command` failed with status code: $exitCode")
  }

  return output.replace("\n", "")
}

fun String.absolutePath(): String {
  return Paths.get(this).toAbsolutePath().toString().replace("\n", "")
}

kotlin {
  val linuxX64 = linuxX64("linuxX64")
  val mingwX64 = mingwX64("mingwX64")

  configure(listOf(linuxX64, mingwX64)) {
    val test: KotlinTaskTestRun<*, *> by testRuns

    test.executionTask.configure {
      testLogging.showStandardStreams = true
    }

    binaries {
      getTest("debug").apply {
        linkerOpts.addAll(cmd("--ldflags").split(" ").filter { it.isNotBlank() })
        linkerOpts.addAll(cmd("--system-libs").split(" ").filter { it.isNotBlank() })
        linkerOpts.addAll(cmd("--libs").split(" ").filter { it.isNotBlank() })
      }
    }
  }

  sourceSets {
    all {
      languageSettings.optIn("kotlin.RequiresOptIn")
    }

    val commonMain by getting {
      dependencies {
        implementation(libs.arrow.core)
        implementation(libs.llvm4k.common)
        implementation(projects.syntax)
        implementation(projects.shared)
        implementation(projects.analyzer)
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(libs.kt.test.common)
        implementation(libs.kt.test.annotations.common)
      }
    }

    val jvmMain by getting {
      dependencies {
        implementation(libs.llvm4k.jvm)
        implementation(libs.bytedeco.llvm)
        implementation(libs.bytedeco.libffi)
        implementation(libs.jna)
      }
    }

    val jvmTest by getting {
      dependencies {
        implementation(libs.kt.test.junit)
        implementation(libs.jupiter.api)
        implementation(libs.jupiter.engine)
      }
    }

    val linuxX64Main by getting {
      dependencies {
        implementation(libs.llvm4k.linuxX64)
      }
    }

    val mingwX64Main by getting {
      dependencies {
        implementation(libs.llvm4k.mingwX64)
      }
    }
  }
}
