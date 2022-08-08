rootProject.name = "plank"

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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
include("frontend:ir")
include("tooling:server")
