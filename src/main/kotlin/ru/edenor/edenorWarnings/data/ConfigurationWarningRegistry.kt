package ru.edenor.edenorWarnings.data

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import ru.edenor.edenorWarnings.EdenorWarnings
import ru.edenor.edenorWarnings.EdenorWarnings.Companion.TEMPLATES_FILENAME
import ru.edenor.edenorWarnings.EdenorWarnings.Companion.TEMPLATES_SECTION
import java.nio.file.Files

class ConfigurationWarningRegistry(plugin: EdenorWarnings) : WarningRegistry {
  private var templatesFile = plugin.dataFolder.resolve(TEMPLATES_FILENAME)
  private lateinit var templatesConfig: YamlConfiguration

  init {
    if(!templatesFile.exists()){
      templatesFile.parentFile?.mkdirs()
      ConfigurationWarningRegistry::class.java.getResourceAsStream("/template.yml")
        .use {s -> Files.copy(s!!, templatesFile.toPath()) }
    }
    reload()
  }

  override fun getTemplates(): List<Warning> {
    val section = templatesConfig.getConfigurationSection(TEMPLATES_SECTION) ?: return listOf()
    return section.getKeys(false)
      .map { k -> section.getConfigurationSection(k)!! }
      .map { s -> readTemplate(s) }
  }

  override fun getTemplate(name: String): Warning? {
    val section = templatesConfig.getConfigurationSection(TEMPLATES_SECTION) ?: return null
    val template = section.getConfigurationSection(name) ?: return null
    return readTemplate(template)
  }

  override fun reload() {
    templatesConfig = YamlConfiguration.loadConfiguration(templatesFile)
  }

  fun readTemplate(section: ConfigurationSection): Warning {
    val warning = Warning(
      section.name,
      section.getString("description"),
      section.getString("title"),
      section.getString("body") ?: throw InvalidConfigurationException("${section.name} has no body."),
      section.getString("permission")
    )

    return warning
  }
}