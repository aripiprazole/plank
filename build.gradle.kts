@file:Suppress("DSL_SCOPE_VIOLATION")

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintPlugin
import org.plank.build.LocalProperties

buildscript {
  repositories {
    maven("https://jitpack.io")
    mavenCentral()
  }

  dependencies {
    classpath(libs.antlr.kotlin.gradle)
  }
}

plugins {
  alias(libs.plugins.kotlin) apply false
  alias(libs.plugins.ktlint) apply false
  alias(libs.plugins.detekt) apply false
}

group = "org.plank"
version = "1.0-SNAPSHOT"

LocalProperties.init(rootProject)

repositories {
  mavenCentral()
}

subprojects {
  apply(plugin = "org.jetbrains.kotlin.multiplatform")
  apply<DetektPlugin>()
  apply<KtlintPlugin>()

  repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.binom.pw")
    maven("https://repo.binom.pw/releases")
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
  }

  group = rootProject.group
  version = rootProject.version

  configure<KtlintExtension> {
    android.set(false)
    additionalEditorconfigFile.set(rootProject.file(".editorconfig"))
  }

  configure<DetektExtension> {
    buildUponDefaultConfig = true
    allRules = false

    config = files("${rootProject.projectDir}/config/detekt.yml")
    baseline = file("${rootProject.projectDir}/config/baseline.xml")
  }

  configure<KotlinMultiplatformExtension> {
    linuxX64("linuxX64")
    mingwX64("mingwX64")

    jvm {
      compilations.all {
        kotlinOptions.jvmTarget = "16"
      }

      testRuns["test"].executionTask.configure {
        useJUnitPlatform()
        testLogging.showStandardStreams = true
        testLogging.exceptionFormat = TestExceptionFormat.FULL
      }
    }

    sourceSets {
      val commonMain by getting
      val commonTest by getting {
        dependencies {
          afterEvaluate {
            implementation(libs.kt.test.common)
            implementation(libs.kt.test.annotations.common)
          }
        }
      }

      val linuxX64Main by getting
      val linuxX64Test by getting

      val mingwX64Main by getting
      val mingwX64Test by getting

      val nativeMain by creating {
        dependsOn(commonMain)
        linuxX64Main.dependsOn(this)
        mingwX64Main.dependsOn(this)
      }
      val nativeTest by creating {
        dependsOn(commonTest)
        linuxX64Test.dependsOn(this)
        mingwX64Test.dependsOn(this)
      }
    }
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xskip-metadata-version-check"
  }

  afterEvaluate {
    val kotlin: KotlinMultiplatformExtension by extensions

    val compilation = kotlin.targets["metadata"].compilations["nativeMain"]

    compilation.compileKotlinTask.doFirst {
      compilation.compileDependencyFiles = compilation.compileDependencyFiles
        .filterNot { it.absolutePath.endsWith("klib/common/stdlib") }
        .let { files(it) }
    }

    kotlin.targets.findByName("jvm")?.apply {
      this as KotlinJvmTarget

      compilations.all {
        kotlinOptions.jvmTarget = "11"
      }

      testRuns["test"].executionTask.configure {
        useJUnitPlatform()
        testLogging.showStandardStreams = true
      }
    }
  }
}
