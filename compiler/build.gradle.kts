import com.lorenzoog.jplank.build.Dependencies

plugins {
  id("org.jetbrains.kotlin.multiplatform")
}

group = "com.lorenzoog"
version = "1.0-SNAPSHOT"

kotlin {
  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(kotlin("stdlib-common"))
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
        implementation(Dependencies.ByteDeco.LLVMPlatform)
        implementation(Dependencies.LLVM4J.LLVM4J)
        implementation(project(":grammar"))
      }
    }
    val jvmTest by getting
  }
}
