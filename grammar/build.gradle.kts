kotlin {
  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(project(":parser"))
        implementation(project(":shared"))
      }
    }
    val commonTest by getting

    val jvmMain by getting {
      dependencies {
        implementation(kotlin("reflect"))
      }
    }
    val jvmTest by getting
  }
}
