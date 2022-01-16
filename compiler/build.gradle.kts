import com.gabrielleeg1.plank.build.Dependencies

plugins {
  id("org.jetbrains.kotlin.multiplatform")
}

group = "com.gabrielleeg1"
version = "1.0-SNAPSHOT"

kotlin {
  jvm {
    testRuns["test"].executionTask.configure {
      useJUnitPlatform()
    }
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(kotlin("stdlib-common"))
        compileOnly(Dependencies.Arrow.Core)
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }

    val jvmMain by getting {
      dependencies {
        compileOnly(Dependencies.ByteDeco.LLVMPlatform)
        compileOnly(Dependencies.LLVM4J.LLVM4J) {
          exclude(group = "org.bytedeco")
        }
        implementation(project(":grammar"))
        implementation(project(":shared"))
        implementation(project(":analyzer"))
      }
    }
    val jvmTest by getting {
      dependencies {
        implementation(Dependencies.ByteDeco.LLVMPlatform)
        implementation(Dependencies.Arrow.Core)
        implementation(Dependencies.LLVM4J.LLVM4J) {
          exclude("org.bytedeco")
        }

        implementation(Dependencies.Jansi.Jansi)

        implementation(kotlin("test-junit"))
        implementation(Dependencies.JUnit.JupiterApi)
        implementation(Dependencies.JUnit.JupiterEngine)
      }
    }
  }
}
