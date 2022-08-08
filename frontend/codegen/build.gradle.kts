kotlin {
  sourceSets {
    all {
      languageSettings.optIn("kotlin.RequiresOptIn")
    }

    val commonMain by getting {
      dependencies {
        implementation(libs.arrow.core)
        implementation(projects.frontend.llvm4k)
        implementation(projects.frontend.syntax)
        implementation(projects.frontend.shared)
        implementation(projects.frontend.analyzer)
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
        implementation(projects.frontend.llvm4k)
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
  }
}
