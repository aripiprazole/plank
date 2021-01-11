plugins {
  `kotlin-dsl`
}

group = "com.lorenzoog"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
}

gradlePlugin {
  plugins.register("composite-build") {
    id = "composite-build"
    implementationClass = "com.lorenzoog.jplank.build.CompositeBuild"
  }
}
