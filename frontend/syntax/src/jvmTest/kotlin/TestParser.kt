package org.plank.syntax

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.plank.syntax.element.PlankFile
import org.plank.syntax.message.CompilerLogger
import pw.binom.io.file.File
import pw.binom.io.file.extension
import pw.binom.io.file.iterator

class TestParser {
  val logger = CompilerLogger(debug = true, verbose = true)

  @Test
  fun `test passes`() {
    val passes = Thread.currentThread().contextClassLoader.getResource("passes").file

    File(passes.toString())
      .iterator()
      .asSequence()
      .filter { it.extension == "plank" }
      .forEach { file ->
        val pFile = PlankFile.of(file, logger = logger)

        logger.info("Testing file ${file.path}")

        when (pFile.isValid) {
          true -> logger.info("  - passed successfully")
          false -> {
            logger.severe("  - didn't pass with ${pFile.violations.size} violations")

            pFile.violations.forEach {
              logger.severe(it.message, it.loc)
            }
          }
        }

        assertTrue(pFile.isValid)
      }
  }

  @Test
  fun `test fail`() {
    val passes = Thread.currentThread().contextClassLoader.getResource("fail").file

    File(passes.toString())
      .iterator()
      .asSequence()
      .filter { it.extension == "plank" }
      .forEach { file ->
        val pFile = PlankFile.of(file, logger = logger)

        logger.info("Testing file ${file.path}")

        when (pFile.isValid) {
          true -> logger.severe("  - passed without any violations")
          false -> {
            logger.info("  - didn't pass with ${pFile.violations.size} violations")

            pFile.violations.forEach {
              logger.severe(it.message, it.loc)
            }
          }
        }

        assertFalse(pFile.isValid)
      }
  }
}
