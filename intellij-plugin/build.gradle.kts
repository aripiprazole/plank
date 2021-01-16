import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.intellij.tasks.PatchPluginXmlTask

plugins {
  id("org.jetbrains.intellij") version "0.6.5"
  id("org.jetbrains.grammarkit") version "2020.3.2"
  kotlin("jvm")
  java
}

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

group = "com.lorenzoog.jplank"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
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
    kotlinOptions.jvmTarget = "1.8"
  }

  val generatePlankGrammar = register<GenerateLexer>("generatePlankGrammar") {
    source = "grammar/plank.flex"
    targetDir = "src/main/gen/com/lorenzoog/jplank/intellijplugin"
    targetClass = "IdeaPlankLexer"
    purgeOldFiles = true
  }

  build {
    dependsOn(generatePlankGrammar)
  }

  getByName<PatchPluginXmlTask>("patchPluginXml") {
    changeNotes(
      """
        Add basic syntax <em>highlight</em>.
      """
    )
  }
}
