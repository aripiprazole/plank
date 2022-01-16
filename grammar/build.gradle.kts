@file:Suppress("UnstableApiUsage")

import com.gabrielleeg1.plank.build.Dependencies
import com.strumenta.antlrkotlin.gradleplugin.AntlrKotlinTask

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
    val commonAntlr by creating {
      dependencies {
        api(kotlin("stdlib-common"))
        api("com.strumenta.antlr-kotlin:antlr-kotlin-runtime:6304d5c1c4")
      }

      kotlin.srcDir("$buildDir/generated-src/commonAntlr/kotlin")
    }

    val commonMain by getting {
      dependsOn(commonAntlr)

      dependencies {
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

tasks {
  val generateGrammarSource = register<AntlrKotlinTask>("generateKotlinGrammarSource") {
    antlrClasspath = configurations.detachedConfiguration(
      project.dependencies.create(Dependencies.Antlr.Antlr4),
      project.dependencies.create(Dependencies.Antlr.AntlrKotlinTarget)
    )
    maxHeapSize = "64m"
    packageName = "com.gabrielleeg1.plank.grammar.generated"
    arguments = listOf("-visitor")
    source = project.objects
      .sourceDirectorySet("commonAntlr", "commonAntlr")
      .srcDir("src/commonAntlr/antlr").apply {
        include("*.g4")
      }
    outputDirectory = File("$buildDir/generated-src/commonAntlr/kotlin")
  }

  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn(generateGrammarSource)
  }

  build {
    dependsOn(generateGrammarSource)
  }
}
