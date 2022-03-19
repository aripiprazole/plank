package org.plank.build

import org.gradle.api.Project
import java.util.Properties

object LocalProperties {
  lateinit var properties: Properties

  fun init(project: Project) {
    properties = Properties()

    val file = project.file("local.properties")

    if (file.exists()) {
      properties.load(file.inputStream())
    }
  }

  fun getOrNull(key: String): String? {
    return properties.getProperty(key)
  }

  inline fun getOrElse(key: String, orElse: () -> String): String {
    return getOrNull(key) ?: orElse()
  }
}
