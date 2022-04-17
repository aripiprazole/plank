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
      getTest("debug").apply {
        linkerOpts.addAll(LlvmConfig.cmd("--ldflags").split(" ").filter { it.isNotBlank() })
        linkerOpts.addAll(LlvmConfig.cmd("--system-libs").split(" ").filter { it.isNotBlank() })
        linkerOpts.addAll(LlvmConfig.cmd("--libs").split(" ").filter { it.isNotBlank() })
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
        implementation(projects.compiler.llvm4k)
        implementation(projects.compiler.syntax)
        implementation(projects.compiler.shared)
        implementation(projects.compiler.analyzer)
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
        implementation(projects.compiler.llvm4k)
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
        implementation(projects.compiler.llvm4k)
      }
    }

    val mingwX64Main by getting {
      dependencies {
        implementation(projects.compiler.llvm4k)
      }
    }
  }
}
