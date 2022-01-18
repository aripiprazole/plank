@file:Suppress("DSL_SCOPE_VIOLATION")

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

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
  alias(libs.plugins.ktlint)
  alias(libs.plugins.detekt)
}

group = "com.gabrielleeg1"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  ktlintRuleset(libs.pinterest.ktlint)
}

subprojects {
  apply(plugin = "org.jetbrains.kotlin.multiplatform")

  repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
    maven("https://repo.binom.pw")
    maven("https://repo.binom.pw/releases")
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
  }

  group = "com.gabrielleeg1"
  version = "1.0-SNAPSHOT"

  configure<KotlinMultiplatformExtension> {
    jvm {
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

ktlint {
  android.set(false)
}

detekt {
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
