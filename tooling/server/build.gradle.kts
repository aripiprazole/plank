plugins {
  id("com.github.johnrengelman.shadow") version "6.1.0"
  java
}

kotlin {
  jvm()

  sourceSets {
    val jvmMain by getting {
      dependencies {
        implementation(projects.compiler.syntax)
        implementation(projects.compiler.shared)
        implementation(projects.compiler.analyzer)
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
    attributes["Main-Class"] = "org.plank.tooling.langserver.MainKt"
  }

  configurations = mutableListOf(jvmMain.compileDependencyFiles as Configuration)
  archiveClassifier.set("all")
}
