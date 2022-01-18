@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation

plugins {
  java
  distribution
}

kotlin {
  sourceSets {
    val commonMain by getting
    val commonTest by getting

    val jvmMain by getting {
      dependencies {
        implementation(kotlin("stdlib-common"))
        implementation(libs.clikt)
        implementation(libs.jansi)
        implementation(libs.bytedeco.llvmplatform)
        implementation(libs.arrow.core)
        implementation(libs.llvm4j)
        implementation(projects.grammar)
        implementation(projects.compiler)
        implementation(projects.shared)
        implementation(projects.analyzer)
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

      from(rootProject.file("LICENSE.txt")) {
        into("licenses")
      }

      from(rootProject.file("licenses/third_party")) {
        into("licenses/third_party")
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
    attributes["Main-Class"] = "com.gabrielleeg1.plank.cli.MainKt"
  }
}
