rootProject.name = "plank"

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    google()
    jcenter()
    gradlePluginPortal()
  }
}

include("codegen")
include("syntax")
include("cli")
include("lang-server")
include("analyzer")
include("shared")
include("parser")
