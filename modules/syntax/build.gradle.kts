kotlin {
  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.mordant)
        implementation(projects.modules.parser)
        implementation(projects.modules.shared)
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
