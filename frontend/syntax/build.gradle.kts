kotlin {
  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.mordant)
        implementation(projects.frontend.syntax.parser)
        implementation(projects.frontend.shared)
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
