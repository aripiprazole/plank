/*
 *    Copyright 2021 Plank
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

@file:Suppress("DSL_SCOPE_VIOLATION")

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.plank.build.LlvmConfig
import org.plank.build.LocalProperties
import org.plank.build.absolutePath
import java.lang.System.getenv

plugins {
  alias(libs.plugins.artifactory)
  `maven-publish`
}

val artifactoryUsername: String = LocalProperties.getOrElse("artifactory.username") {
  getenv("ARTIFACTORY_USERNAME").orEmpty()
}

val artifactoryPassword: String = LocalProperties.getOrElse("artifactory.password") {
  getenv("ARTIFACTORY_PASSWORD").orEmpty()
}

artifactory {
  setContextUrl("https://plank.jfrog.io/artifactory")

  publish {
    repository {
      setRepoKey("default-gradle-dev-local")
      setUsername(artifactoryUsername)
      setPassword(artifactoryPassword)
      setMavenCompatible(true)
    }

    defaults {
      setPublishArtifacts(true)
      setPublishPom(true)
      publications("jvm", "linuxX64", "mingwX64", "js", "kotlinMultiplatform")
    }
  }
}

configure<KotlinMultiplatformExtension> {
  explicitApi()

  jvm {
    withJava()
    compilations.all {
      kotlinOptions.jvmTarget = "16"
    }
    testRuns["test"].executionTask.configure {
      useJUnitPlatform()
    }
  }

  val linuxX64 = linuxX64("linuxX64")
  val mingwX64 = mingwX64("mingwX64")

  configure(listOf(linuxX64, mingwX64)) {
    val main by compilations.getting
    if (LlvmConfig.hasLlvm()) {
      val llvm by main.cinterops.creating {
        includeDirs(LlvmConfig.cmd("--includedir").absolutePath())
      }
    }
  }

  sourceSets {
    val commonMain by getting
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }

    val jvmMain by getting {
      dependencies {
        implementation(libs.bytedeco.llvm)
        implementation(libs.bytedeco.libffi)
        implementation(libs.jna)
      }
    }
  }
}
