import com.lorenzoog.jplank.build.Dependencies

group = "com.lorenzoog"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

kotlin {
  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(kotlin("stdlib-common"))
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }

    val jvmMain by getting {
      dependencies {
        implementation(Dependencies.ByteDeco.LLVMPlatform)
        implementation(Dependencies.BitBuilder.BitBuilder)
        implementation(project(":grammar"))
      }
    }
    val jvmTest by getting
  }
}
