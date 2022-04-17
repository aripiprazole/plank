kotlin {
  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.mordant)
        implementation(projects.compiler.parser)
        implementation(projects.compiler.shared)
      }
    }

    val jvmMain by getting {
      dependencies {
        implementation(libs.kt.reflect)
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
