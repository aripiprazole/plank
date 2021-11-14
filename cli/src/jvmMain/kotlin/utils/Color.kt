@file:Suppress("unused")

package com.gabrielleeg1.plank.cli.utils

import org.fusesource.jansi.Ansi

fun String.black(): String {
  return Ansi.ansi().fg(Ansi.Color.BLACK).toString() + this + Ansi().reset()
}

fun String.green(): String {
  return Ansi.ansi().fg(Ansi.Color.GREEN).toString() + this + Ansi().reset()
}

fun String.red(): String {
  return Ansi.ansi().fg(Ansi.Color.RED).toString() + this + Ansi().reset()
}

fun String.yellow(): String {
  return Ansi.ansi().fg(Ansi.Color.YELLOW).toString() + this + Ansi().reset()
}

fun String.blue(): String {
  return Ansi.ansi().fg(Ansi.Color.BLUE).toString() + this + Ansi().reset()
}

fun String.cyan(): String {
  return Ansi.ansi().fg(Ansi.Color.CYAN).toString() + this + Ansi().reset()
}

fun String.white(): String {
  return Ansi.ansi().fg(Ansi.Color.WHITE).toString() + this + Ansi().reset()
}

fun String.bold(): String {
  return Ansi.ansi().bold().toString() + this + Ansi().reset()
}

fun String.italic(): String {
  return Ansi.ansi().a(Ansi.Attribute.ITALIC).toString() + this + Ansi().reset()
}

fun String.underline(): String {
  return Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString() + this + Ansi().reset()
}

fun String.strikethrough(): String {
  return Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString() + this + Ansi().reset()
}

fun String.reset(): String {
  return Ansi.ansi().a(Ansi.Attribute.RESET).toString() + this + Ansi().reset()
}
