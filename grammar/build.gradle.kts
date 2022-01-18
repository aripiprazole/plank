@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.kotlin.multiplatform")
}

group = "com.gabrielleeg1"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven("https://jitpack.io")
}

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
