rootProject.name = "plank"

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
  }
}

include("modules:codegen")
include("modules:llvm4k")
include("modules:syntax")
include("modules:cli")
include("modules:lang-server")
include("modules:analyzer")
include("modules:shared")
include("modules:parser")
