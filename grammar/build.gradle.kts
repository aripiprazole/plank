@file:Suppress("UnstableApiUsage")

import com.lorenzoog.jplank.build.Dependencies
import com.strumenta.antlrkotlin.gradleplugin.AntlrKotlinTask

plugins {
  id("org.jetbrains.kotlin.multiplatform")
}

group = "com.lorenzoog"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

kotlin {
  jvm()

  sourceSets {
    val commonAntlr by creating {
      dependencies {
        api(kotlin("stdlib-common"))
        api("com.strumenta.antlr-kotlin:antlr-kotlin-runtime:-SNAPSHOT")
      }

      kotlin.srcDir("$buildDir/generated-src/commonAntlr/kotlin")
    }

    val commonMain by getting {
      dependsOn(commonAntlr)

      dependencies {
        api(Dependencies.Binom.File)
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
    packageName = "com.lorenzoog.jplank.grammar.generated"
    arguments = listOf("-visitor")
    source = project.objects
      .sourceDirectorySet("commonAntlr", "commonAntlr")
      .srcDir("src/commonAntlr/antlr").apply {
        include("*.g4")
      }
    outputDirectory = File("$buildDir/generated-src/commonAntlr/kotlin")
  }

  build {
    dependsOn(generateGrammarSource)
  }
}
