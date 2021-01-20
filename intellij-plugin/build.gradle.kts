import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser
import org.jetbrains.intellij.tasks.PatchPluginXmlTask

plugins {
  id("org.jetbrains.intellij") version "0.6.5"
  id("org.jetbrains.grammarkit") version "2020.3.2"
  id("com.github.johnrengelman.shadow") version "6.1.0"
  kotlin("jvm")
  java
}

java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

group = "com.lorenzoog.jplank"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":grammar"))
  testImplementation("junit", "junit", "4.12")
}

sourceSets["main"].java.srcDirs("src/main/gen")

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
  version = "2020.3.1"
}

grammarKit {
  jflexRelease = "1.7.0-1"
  grammarKitRelease = "6452fde524"
}

tasks {
  withType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile> {
    kotlinOptions.jvmTarget = "11"
  }

  val generatePlankParser = register<GenerateParser>("generatePlankParser") {
    source = "grammar/plank.bnf"
    targetRoot = "src/main/gen"
    pathToParser = "com/lorenzoog/jplank/intellijplugin/parser/PlankParser.java"
    pathToPsiRoot = "com/lorenzoog/jplank/intellijplugin/psi"
    purgeOldFiles = true
  }

  val generatePlankLexer = register<GenerateLexer>("generatePlankLexer") {
    source = "grammar/plank.flex"
    targetDir = "src/main/gen/com/lorenzoog/jplank/intellijplugin/lexer"
    targetClass = "IdeaPlankLexer"
    purgeOldFiles = true
  }

  compileKotlin {
    dependsOn(generatePlankLexer)
  }

  getByName<PatchPluginXmlTask>("patchPluginXml") {
    changeNotes(
      """
        Add basic syntax <em>highlight</em>.
      """
    )
  }
}
