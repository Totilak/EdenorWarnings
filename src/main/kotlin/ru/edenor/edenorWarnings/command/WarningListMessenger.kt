package ru.edenor.edenorWarnings.command

import org.bukkit.command.CommandSender
import ru.edenor.edenorWarnings.data.WarningRegistry

object WarningListMessenger {
  fun sendList(sender: CommandSender, registry: WarningRegistry) {
    val warnings = registry.getTemplates().sortedBy { it.name }

    if (warnings.isEmpty()) {
      sender.sendRichMessage("<red>Список пуст!")
      return
    }

    sender.sendRichMessage("<dark_aqua>Список шаблонов")
    warnings.forEach { (name, description, _, _, permission) ->
      if (permission != null && !sender.hasPermission(permission)) return@forEach

      sender.sendRichMessage(clickableTemplate(name, description))
    }
  }

  private fun clickableTemplate(name: String, description: String?): String =
    "<green><hover:show_text:'Нажми, чтобы использовать'>" +
        "<click:suggest_command:/ew send $name >$name</click></hover></green>" +
        "<yellow> - ${description ?: "none"}</yellow>"
}