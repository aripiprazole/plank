import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

buildscript {
  repositories {
    maven("https://jitpack.io")
  }

  dependencies {
    classpath("com.strumenta.antlr-kotlin:antlr-kotlin-gradle-plugin:-SNAPSHOT")
  }
}

plugins {
  kotlin("multiplatform") version "1.4.30-M1"
  id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
  id("composite-build")
}

group = "com.lorenzoog"
version = "1.0-SNAPSHOT"

allprojects {
  apply(plugin = "org.jetbrains.kotlin.multiplatform")
  apply(plugin = "org.jlleitschuh.gradle.ktlint")
  apply(plugin = "composite-build")

  repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
    maven("https://repo.binom.pw/releases")
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
  }

  ktlint {
    android.set(true)
  }

  tasks.withType<KotlinJvmCompile> {
    kotlinOptions.jvmTarget = "11"
  }
}

kotlin {
  jvm()

  sourceSets {
    val commonMain by getting
    val commonTest by getting

    val jvmMain by getting
    val jvmTest by getting
  }
}
