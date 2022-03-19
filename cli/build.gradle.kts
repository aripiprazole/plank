import org.jetbrains.kotlin.gradle.testing.KotlinTaskTestRun
import org.plank.build.LlvmConfig

kotlin {
  val linuxX64 = linuxX64("linuxX64")
  val mingwX64 = mingwX64("mingwX64")

  configure(listOf(linuxX64, mingwX64)) {
    val test: KotlinTaskTestRun<*, *> by testRuns

    test.executionTask.configure {
      testLogging.showStandardStreams = true
    }

    binaries {
      executable("plank") {
        linkerOpts.addAll(LlvmConfig.cmd("--ldflags").split(" ").filter { it.isNotBlank() })
        linkerOpts.addAll(LlvmConfig.cmd("--system-libs").split(" ").filter { it.isNotBlank() })
        linkerOpts.addAll(LlvmConfig.cmd("--libs").split(" ").filter { it.isNotBlank() })
        entryPoint = "org.plank.cli.main"
      }
    }
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.arrow.core)
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(libs.kt.test.common)
        implementation(libs.kt.test.annotations.common)
      }
    }

    val nativeMain by getting {
      dependencies {
        implementation(projects.syntax)
        implementation(projects.shared)
        implementation(projects.analyzer)
        implementation(projects.codegen)
        implementation(projects.llvm4k)
        implementation(libs.clikt)
      }
    }

    val linuxX64Main by getting {
      dependencies {
        implementation(projects.llvm4k)
      }
    }

    val mingwX64Main by getting {
      dependencies {
        implementation(projects.llvm4k)
      }
    }
  }
}
