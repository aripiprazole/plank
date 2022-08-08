rootProject.name = "plank"

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
  }
}

include("frontend:codegen")
include("frontend:llvm4k")
include("frontend:syntax")
include("frontend:cli")
include("frontend:analyzer")
include("frontend:shared")
include("frontend:syntax:parser")
include("tooling:server")
