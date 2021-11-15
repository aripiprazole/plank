import com.gabrielleeg1.plank.build.Dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

buildscript {
  repositories {
    maven("https://jitpack.io")
    mavenCentral()
  }

  dependencies {
    classpath("com.strumenta.antlr-kotlin:antlr-kotlin-gradle-plugin:-SNAPSHOT")
  }
}

plugins {
  kotlin("multiplatform") version "1.4.32" apply false
  id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
  id("io.gitlab.arturbosch.detekt") version "1.16.0"
  id("composite-build")
}

group = "com.gabrielleeg1"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  ktlintRuleset(Dependencies.Pinterest.Ktlint)
}

subprojects {
  apply(plugin = "org.jlleitschuh.gradle.ktlint")
  apply(plugin = "io.gitlab.arturbosch.detekt")
  apply(plugin = "composite-build")

  repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
    maven("https://repo.binom.pw")
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
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

  tasks.withType<KotlinJvmCompile> {
    kotlinOptions.jvmTarget = "11"
  }
}
