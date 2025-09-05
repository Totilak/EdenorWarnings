package ru.edenor.edenorWarnings

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin
import ru.edenor.edenorWarnings.command.EdenorWarningsCommand
import ru.edenor.edenorWarnings.data.ConfigurationWarningRegistry
import ru.edenor.edenorWarnings.data.WarningRegistry

class EdenorWarnings : JavaPlugin() {
  lateinit var warningRegistry: WarningRegistry

  override fun onEnable() {
    // Plugin startup logic
    instance = this
    EdenorWarnings.instance.slF4JLogger.info("EdenorWarnings is active!")

    warningRegistry = ConfigurationWarningRegistry(this)

    lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { commands
      ->
      EdenorWarningsCommand(this, warningRegistry).commands().forEach {
        commands.registrar().register(it)
      }
    }
  }

  override fun onDisable() {
    // Plugin shutdown logic
  }

  fun reload() {
    warningRegistry.reload()
  }

  companion object {
    const val HELPER_PERMISSION = "ew.helper"
    const val MODERATOR_PERMISSION = "ew.moderator"
    const val ADMINISTRATOR_PERMISSION = "ew.administrator"
    const val NOTIFY_PERMISSION = "ew.notify"
    const val TEMPLATES_FILENAME = "temlates.yml"
    const val TEMPLATES_SECTION = "templates"

    lateinit var instance: EdenorWarnings
      private set
  }
}
