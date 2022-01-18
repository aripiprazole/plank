rootProject.name = "plank"

enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
  repositories {
    google()
    jcenter()
    gradlePluginPortal()
  }
}

include("compiler")
include("grammar")
include("cli")
include("lang-server")
include("analyzer")
include("shared")
include("parser")
