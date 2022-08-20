import com.strumenta.antlrkotlin.gradleplugin.AntlrKotlinTask

kotlin {
  jvm()

  sourceSets {
    val commonAntlr by creating {
      dependencies {
        api(libs.antlr.kotlin.runtime)
      }
    }

    val commonMain by getting {
      dependsOn(commonAntlr)

      dependencies {
        implementation(projects.frontend.shared)
      }

      kotlin.srcDir(buildDir.resolve("generated-src").resolve("commonAntlr").resolve("kotlin"))
    }

    val jvmMain by getting {
      dependencies {
        implementation(libs.kt.reflect)
      }
    }
  }
}

tasks {
  val antlr4Version = libs.versions.antlr4.get()
  val antlrKotlinVersion = libs.versions.antlr.kotlin.get()

  val generateParserSource by creating(AntlrKotlinTask::class) {
    val dependencies = project.dependencies

    antlrClasspath = configurations.detachedConfiguration(
      dependencies.create("org.antlr:antlr4:$antlr4Version"),
      dependencies.create("com.strumenta.antlr-kotlin:antlr-kotlin-target:$antlrKotlinVersion"),
    )
    maxHeapSize = "64m"
    arguments = listOf("-package", "org.plank.syntax.parser")
    source = project.objects
      .sourceDirectorySet("commonAntlr", "commonAntlr")
      .srcDir("src/commonAntlr/antlr").apply {
        include("*.g4")
      }
    outputDirectory = buildDir.resolve("generated-src").resolve("commonAntlr").resolve("kotlin")
  }
}
