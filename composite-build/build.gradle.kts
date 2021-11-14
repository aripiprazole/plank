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
    implementationClass = "com.gabrielleeg1.plank.build.CompositeBuild"
  }
}
