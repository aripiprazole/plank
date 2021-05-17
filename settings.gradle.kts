rootProject.name = "plank"

enableFeaturePreview("GRADLE_METADATA")
pluginManagement {
  repositories {
    google()
    jcenter()
    gradlePluginPortal()
  }
}

includeBuild("composite-build")
include("compiler")
include("grammar")
include("cli")
include("lang-server")
include("analyzer")
include("shared")
