rootProject.name = "jplank"

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
include("intellij-plugin")
include("lang-server")
include("vscode-plugin")
include("analyzer")
