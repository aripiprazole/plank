kotlin {
  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        api(libs.okio)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(libs.kt.test.common)
        implementation(libs.kt.test.annotations.common)
      }
    }
  }
}
