kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(kotlin("stdlib-common"))
        implementation(project(":grammar"))
        implementation(project(":shared"))
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(libs.arrow.core)
        implementation(kotlin("stdlib-common"))
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
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
