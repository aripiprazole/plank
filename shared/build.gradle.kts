import com.lorenzoog.plank.build.Dependencies

plugins {
  kotlin("multiplatform")
}

group = "com.lorenzoog"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven("https://repo.binom.pw/releases")
}

kotlin {
  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(kotlin("stdlib-common"))
        api(Dependencies.Binom.File)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }
  }
}
