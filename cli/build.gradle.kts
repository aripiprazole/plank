import com.lorenzoog.jplank.build.Dependencies

plugins {
  id("com.github.johnrengelman.shadow") version "6.1.0"
  java
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
        implementation(Dependencies.Clikt.Clikt)
        implementation(Dependencies.Jansi.Jansi)
        implementation(Dependencies.ByteDeco.LLVMPlatform)
        implementation(Dependencies.BitBuilder.BitBuilder)
        implementation(project(":grammar"))
        implementation(project(":compiler"))
      }
    }
  }
}

tasks.shadowJar {
  val jvmMain = kotlin.jvm().compilations.getByName("main")

  from(jvmMain.output)
  manifest {
    attributes["Main-Class"] = "com.lorenzoog.jplank.MainKt"
  }

  configurations = mutableListOf(jvmMain.compileDependencyFiles as Configuration)
  archiveClassifier.set("all")
}

tasks.build {
  dependsOn(tasks.shadowJar)
}
