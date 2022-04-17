kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(projects.compiler.syntax)
        implementation(projects.compiler.shared)
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
