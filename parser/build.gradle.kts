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
        implementation(projects.shared)
      }

      kotlin.srcDir(buildDir.resolve("generated-src").resolve("commonAntlr").resolve("kotlin"))
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

  val generateParserSource by creating(AntlrKotlinTask::class) {
    val dependencies = project.dependencies

    antlrClasspath = configurations.detachedConfiguration(
      dependencies.create("org.antlr:antlr4:$antlr4Version"),
      dependencies.create("com.strumenta.antlr-kotlin:antlr-kotlin-target:$antlrKotlinVersion")
    )
    maxHeapSize = "64m"
    arguments = listOf("-visitor")
    source = project.objects
      .sourceDirectorySet("commonAntlr", "commonAntlr")
      .srcDir("src/commonAntlr/antlr").apply {
        include("*.g4")
      }
    outputDirectory = buildDir.resolve("generated-src").resolve("commonAntlr").resolve("kotlin")

    finalizedBy(ktlintFormat)
  }

  runKtlintFormatOverCommonMainSourceSet {
    inputs.dir(generateParserSource.outputDirectory.absoluteFile)
  }

  kotlin.targets
    .flatMap { it.compilations }
    .map { it.compileKotlinTask }
    .forEach { task ->
      task.dependsOn(generateParserSource)
    }
}
