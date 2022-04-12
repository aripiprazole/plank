kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(projects.modules.syntax)
        implementation(projects.modules.shared)
      }
    }

    val jvmTest by getting {
      dependencies {
        implementation(kotlin("test-junit"))
        implementation(libs.jupiter.api)
        implementation(libs.jupiter.engine)
      }
    }
  }
}
