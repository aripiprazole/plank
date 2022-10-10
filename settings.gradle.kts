rootProject.name = "plank"

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
  }
}

include("frontend:syntax")
include("frontend:analyzer")
include("frontend:shared")
include("frontend:syntax:parser")
include("tooling:server")
