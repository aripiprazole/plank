rootProject.name = "plank"

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
  }
}

include("compiler:codegen")
include("compiler:llvm4k")
include("compiler:syntax")
include("compiler:cli")
include("compiler:analyzer")
include("compiler:shared")
include("compiler:parser")
include("tooling:server")
