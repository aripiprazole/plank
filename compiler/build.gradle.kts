kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        compileOnly(libs.arrow.core)
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
        compileOnly(libs.bytedeco.llvmplatform)
        compileOnly(libs.llvm4j)
        implementation(libs.kt.reflect)
        implementation(projects.grammar)
        implementation(projects.shared)
        implementation(projects.analyzer)
      }
    }
    val jvmTest by getting {
      dependencies {
        implementation(libs.kt.reflect)
        implementation(libs.bytedeco.llvmplatform)
        implementation(libs.arrow.core)
        implementation(libs.llvm4j)

        implementation(libs.kt.test.junit)
        implementation(libs.jupiter.api)
        implementation(libs.jupiter.engine)
      }
    }
  }
}
