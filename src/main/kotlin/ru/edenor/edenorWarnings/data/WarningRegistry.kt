package ru.edenor.edenorWarnings.data

interface WarningRegistry {
  fun getTemplates(): List<Warning>
  fun getTemplate(name: String): Warning?
  /*TODO Add setTemplate*/
  fun reload()
}