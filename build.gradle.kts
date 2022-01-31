@file:Suppress("DSL_SCOPE_VIOLATION")

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintPlugin

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

group = "com.gabrielleeg1"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  jcenter()
}

subprojects {
  apply(plugin = "org.jetbrains.kotlin.multiplatform")
  apply<DetektPlugin>()
  apply<KtlintPlugin>()

  repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
    maven("https://repo.binom.pw")
    maven("https://repo.binom.pw/releases")
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://plank.jfrog.io/artifactory/default-gradle-dev-local/")
  }

  group = "com.gabrielleeg1"
  version = "1.0-SNAPSHOT"

  configure<KtlintExtension> {
    android.set(false)
  }

  configure<DetektExtension> {
    buildUponDefaultConfig = true
    allRules = false

    config = files("${rootProject.projectDir}/config/detekt.yml")
    baseline = file("${rootProject.projectDir}/config/baseline.xml")

    reports {
      html.enabled = true
      xml.enabled = true
      txt.enabled = true
      sarif.enabled = true
    }
  }

  configure<KotlinMultiplatformExtension> {
    val hostOs: String = System.getProperty("os.name")
    val isMingwX64: Boolean = hostOs.startsWith("Windows")

    jvm {
      compilations.all {
        kotlinOptions.jvmTarget = "11"
      }

      testRuns["test"].executionTask.configure {
        useJUnitPlatform()
        testLogging.showStandardStreams = true
      }
    }

    when {
      hostOs == "Mac OS X" -> macosX64("native")
      hostOs == "Linux" -> linuxX64("native")
      isMingwX64 -> mingwX64("native")
      else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xskip-metadata-version-check"
  }
}
