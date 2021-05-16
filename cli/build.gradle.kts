@file:Suppress("UnstableApiUsage")

import com.lorenzoog.plank.build.Dependencies
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation

plugins {
  id("org.jetbrains.kotlin.multiplatform")
  java
  distribution
}

group = "com.lorenzoog"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

kotlin {
  jvm {
    java.sourceCompatibility = JavaVersion.VERSION_11
    java.targetCompatibility = JavaVersion.VERSION_11
  }

  sourceSets {
    val commonMain by getting
    val commonTest by getting

    val jvmMain by getting {
      dependencies {
        implementation(kotlin("stdlib-common"))
        implementation(Dependencies.Clikt.Clikt)
        implementation(Dependencies.Jansi.Jansi)
        implementation(Dependencies.ByteDeco.LLVMPlatform)
        implementation(Dependencies.LLVM4J.LLVM4J)
        implementation(project(":grammar"))
        implementation(project(":compiler"))
        implementation(project(":shared"))
        implementation(project(":analyzer"))
      }
    }
  }
}

val jvmMain: KotlinJvmCompilation = kotlin.jvm().compilations.getByName("main")
val arch: String = System.getProperty("os.arch")
val hostOs: String = System.getProperty("os.name")
val isMingwX64: Boolean = hostOs.startsWith("Windows")

fun CopySpec.excludeOsArch(
  os: String,
  vararg architectures: String,
  target: String = os.toLowerCase()
) {
  if (!hostOs.startsWith(os)) {
    architectures.forEach {
      if (arch != it) {
        exclude("*-$target-$it.jar")
      }
    }
  }
}

fun CopySpec.includeLlvm() {
  excludeOsArch("macosx", "x86_64", "arm64")
  excludeOsArch("macosx", "arm64", "x86_64", target = "ios")

  excludeOsArch("Linux", "armhf", "arm64", "ppc64le", "x86", "x86_64")

  excludeOsArch("Windows", "x86", "x86_64")
}

distributions {
  main {
    distributionBaseName.set("plank")

    contents {
      from(rootProject.file("README.md"))
      from(rootProject.file("LICENSE.txt"))

      from(rootProject.file("licenses")) {
        into("licenses")
      }

      from(rootProject.file("stdlib")) {
        into("stdlib")
      }

      from(rootProject.file("runtime")) {
        into("runtime")
      }

      from(rootProject.file("bin")) {
        into("bin")
      }

      from(tasks.jar) {
        into("libs")
      }

      from(jvmMain.runtimeDependencyFiles) {
        includeLlvm()

        into("libs")
      }
    }
  }
}

tasks.jar {
  from(jvmMain.output)
  manifest {
    attributes["Main-Class"] = "com.lorenzoog.plank.cli.MainKt"
  }
}
