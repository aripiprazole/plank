kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.mordant)
        implementation(projects.parser)
        implementation(projects.shared)
      }
    }
    val commonTest by getting

    val jvmMain by getting {
      dependencies {
        implementation(libs.kt.reflect)
      }
    }
    val jvmTest by getting
  }
}
