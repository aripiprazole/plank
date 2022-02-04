kotlin {
  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(projects.syntax)
        implementation(projects.shared)
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
