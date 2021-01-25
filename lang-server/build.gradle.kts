import com.lorenzoog.jplank.build.Dependencies

plugins {
  kotlin("multiplatform")
  id("com.github.johnrengelman.shadow") version "6.1.0"
  java
}

group = "com.lorenzoog"
version = "1.0-SNAPSHOT"

kotlin {
  jvm()

  /*
   * Targets configuration omitted.
   * To find out how to configure the targets, please follow the link:
   * https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets
   */
  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(kotlin("stdlib-common"))
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }

    val jvmMain by getting {
      dependencies {
        implementation(project(":grammar"))
        implementation(project(":analyzer"))
        implementation(Dependencies.Kotlin.Coroutines)
        implementation(Dependencies.Kotlin.CoroutinesJdk8)
        implementation(Dependencies.Eclipse4J.Eclipse4J)
        implementation(Dependencies.Eclipse4J.JsonRPC)
      }
    }
  }
}

tasks.shadowJar {
  val jvmMain = kotlin.jvm()
    .compilations
    .getByName("main")

  from(jvmMain.output)
  manifest {
    attributes["Main-Class"] = "com.lorenzoog.jplank.tooling.langserver.MainKt"
  }

  configurations = mutableListOf(jvmMain.compileDependencyFiles as Configuration)
  archiveClassifier.set("all")
}
