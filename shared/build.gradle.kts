kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(kotlin("stdlib-common"))
        api(libs.binom.file)
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
