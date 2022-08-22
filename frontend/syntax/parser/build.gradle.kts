import com.strumenta.antlrkotlin.gradleplugin.AntlrKotlinTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.plank.build.KtSuppressFilterReader

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

val antlr4Version = libs.versions.antlr4.get()
val antlrKotlinVersion = libs.versions.antlr.kotlin.get()

val generateCommonParserSource by tasks.creating(AntlrKotlinTask::class) {
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
  outputDirectory = buildDir.resolve("generated-temp").resolve("commonAntlr").resolve("kotlin")
}

// Workaround to suppress Kotlin, Detekt and Ktlint warnings in generated parser source.
// The original issue is at https://github.com/Strumenta/antlr-kotlin/issues/36.
val generateAntlrSource by tasks.creating(Copy::class) {
  dependsOn(generateCommonParserSource)
  from(buildDir.resolve("generated-temp").resolve("commonAntlr").resolve("kotlin"))
  include("**/*.kt")
  filter<KtSuppressFilterReader>()
  into(buildDir.resolve("generated-src").resolve("commonAntlr").resolve("kotlin"))
}

tasks.withType<KotlinCompile> { dependsOn(generateAntlrSource) }
