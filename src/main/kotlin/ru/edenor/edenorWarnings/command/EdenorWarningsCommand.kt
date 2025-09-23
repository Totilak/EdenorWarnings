package ru.edenor.edenorWarnings.command

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver
import org.bukkit.command.CommandSender
import ru.edenor.edenorWarnings.EdenorWarnings
import ru.edenor.edenorWarnings.EdenorWarnings.Companion.ADMINISTRATOR_PERMISSION
import ru.edenor.edenorWarnings.EdenorWarnings.Companion.HELPER_PERMISSION
import ru.edenor.edenorWarnings.command.CommandExtensions.requiresAnyPermission
import ru.edenor.edenorWarnings.command.CommandExtensions.requiresPermission
import ru.edenor.edenorWarnings.command.CommandExtensions.simplyRun
import ru.edenor.edenorWarnings.data.WarningRegistry

class EdenorWarningsCommand(
  private val plugin: EdenorWarnings,
  private val warningRegistry: WarningRegistry
) {
  fun commands() = arrayOf(ew)

  private val listSection =
    literal("list").requiresAnyPermission().simplyRun(::sendList)

  private val useSection =
    literal("send").requiresAnyPermission()
      .then(
        argument("template", WarningNotificationsArgumentType(warningRegistry))
          .then(
            argument("name", ArgumentTypes.playerProfiles())
              .simplyRun(::sendWarning)
          )
      )

  private val reloadSection =
    literal("reload").requiresPermission(ADMINISTRATOR_PERMISSION).simplyRun(::reload)

  private val ew =
    literal("ew")
      .requiresAnyPermission()
      .simplyRun(::sendHelp)
      .then(listSection)
      .then(useSection)
      .then(reloadSection)
      .build()

  private fun reload(sender: CommandSender) {
    plugin.reload()
    sender.sendRichMessage("<green>Настройки успешно перезагружены")
  }

  private fun sendList(sender: CommandSender) {
    WarningListMessenger.sendList(sender, warningRegistry)
  }


  private fun sendWarning(context: CommandContext<CommandSourceStack>) {
    val profiles = context
      .getArgument("name", PlayerProfileListResolver::class.java)
      .resolve(context.source)

    val template = WarningNotificationsArgumentType.getArgument("template", context)
    val sender = context.source.sender
    val warning = warningRegistry.getTemplate(template) ?: run {
      sender.sendRichMessage("<red>Шаблон $template не найден!</red>")
      return
    }

    profiles.forEach { profile ->
      Message.sendWarning(warning, sender, profile, plugin)
    }
  }

  private fun sendHelp(sender: CommandSender) {

    sender.sendRichMessage(
      "<b><red>EdenorWarnings</red></b> <gray>(${plugin.pluginMeta.version})</gray> <dark_aqua>- Оповещает игрока предзаписанными сообщениями</dark_aqua>"
    )

    if (sender.hasPermission(HELPER_PERMISSION)) {
      sender.sendRichMessage(
        "<green><hover:show_text:'Нажми, чтобы использовать'><click:suggest_command:/ew list>/ew list</click></hover> <yellow>- Вывести список сообщений"
      )
      sender.sendRichMessage(
        "<green><hover:show_text:'Нажми, чтобы использовать'><click:suggest_command:/ew send>/ew send</click></hover> <warning> <username> <yellow>- Отправить сообщение игроку"
      )
    }
    /*    if (sender.hasPermission(MODERATOR_PERMISSION)) {
          sender.sendRichMessage(
            "Хелп + модер команды"
          )
        }*/
    if (sender.hasPermission(ADMINISTRATOR_PERMISSION)) {
      sender.sendRichMessage(
        "<green><hover:show_text:'Нажми, чтобы использовать'><click:suggest_command:/ew reload>/ew reload</click></hover> <yellow>- Перезагрузить настройки"
      )
    }
  }


}