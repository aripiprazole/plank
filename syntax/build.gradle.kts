kotlin {
  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.mordant)
        implementation(projects.parser)
        implementation(projects.shared)
      }
    }

    val jvmMain by getting {
      dependencies {
        implementation(libs.kt.reflect)
      }
    }
  }
}
