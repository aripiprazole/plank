kotlin {
  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(kotlin("stdlib-common"))
        compileOnly(libs.arrow.core)
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
        compileOnly(libs.bytedeco.llvmplatform)
        compileOnly(libs.llvm4j)
        implementation(kotlin("reflect"))
        implementation(project(":grammar"))
        implementation(project(":shared"))
        implementation(project(":analyzer"))
      }
    }
    val jvmTest by getting {
      dependencies {
        implementation(kotlin("reflect"))
        implementation(libs.bytedeco.llvmplatform)
        implementation(libs.arrow.core)
        implementation(libs.llvm4j)

        implementation(libs.jansi)

        implementation(kotlin("test-junit"))
        implementation(libs.jupiter.api)
        implementation(libs.jupiter.engine)
      }
    }
  }
}
