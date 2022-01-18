plugins {
  id("com.github.johnrengelman.shadow") version "6.1.0"
  java
}

kotlin {
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
        implementation(projects.grammar)
        implementation(projects.shared)
        implementation(projects.analyzer)
        implementation(libs.ktx.coroutines.core)
        implementation(libs.ktx.coroutines.jdk8)
        implementation(libs.lsp4j.lsp4j)
        implementation(libs.lsp4j.jsonrpc)
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
    attributes["Main-Class"] = "com.gabrielleeg1.plank.tooling.langserver.MainKt"
  }

  configurations = mutableListOf(jvmMain.compileDependencyFiles as Configuration)
  archiveClassifier.set("all")
}
