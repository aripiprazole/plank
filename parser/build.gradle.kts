import com.strumenta.antlrkotlin.gradleplugin.AntlrKotlinTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

kotlin {
  sourceSets {
    val commonAntlr by creating {
      dependencies {
        api(libs.antlr.kotlin.runtime)
      }
    }

    val commonMain by getting {
      dependsOn(commonAntlr)

      dependencies {
        implementation(projects.shared)
      }

      kotlin.srcDir("$buildDir/generated-src/commonAntlr/kotlin")
    }
    val commonTest by getting

    val jvmMain by getting {
      dependencies {
        implementation(libs.kt.reflect)
      }
    }
    val jvmTest by getting
  }
}

tasks {
  val antlr4Version = libs.versions.antlr4.get()
  val antlrKotlinVersion = libs.versions.antlr.kotlin.get()

  val generateKotlinGrammarSource = register<AntlrKotlinTask>("generateKotlinGrammarSource") {
    val dependencies = project.dependencies

    antlrClasspath = configurations.detachedConfiguration(
      dependencies.create("org.antlr:antlr4:$antlr4Version"),
      dependencies.create("com.strumenta.antlr-kotlin:antlr-kotlin-target:$antlrKotlinVersion")
    )
    maxHeapSize = "64m"
    packageName = "com.gabrielleeg1.plank.parser"
    arguments = listOf("-visitor")
    source = project.objects
      .sourceDirectorySet("commonAntlr", "commonAntlr")
      .srcDir("src/commonAntlr/antlr").apply {
        include("*.g4")
      }
    outputDirectory = File("$buildDir/generated-src/commonAntlr/kotlin")
  }

  withType<KotlinCompile> {
    dependsOn(generateKotlinGrammarSource)
  }
}
