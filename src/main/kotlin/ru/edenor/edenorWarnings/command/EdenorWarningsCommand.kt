package ru.edenor.edenorWarnings.command

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.Sound
import org.bukkit.SoundCategory
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
  fun commands(): Array<LiteralCommandNode<CommandSourceStack>> {
    return arrayOf(ew)
  }


  private val listSection =
    literal("list").requiresPermission(HELPER_PERMISSION).simplyRun(::sendList)


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
    val warnings = warningRegistry.getTemplates().sortedBy { it.name }

    if (warnings.isEmpty()) {
      sender.sendRichMessage("<red>Список пуст!")
    }

    sender.sendRichMessage("<dark_aqua>Список шаблонов")
    for ((name, description, _, _, permission) in warnings) {
      if (permission != null && !sender.hasPermission(permission)) {
        continue
      }

      sender.sendRichMessage(
        "<green><hover:show_text:'Нажми, чтобы использовать'><click:suggest_command:/ew send $name >$name</click></hover></green><yellow> - $description</yellow>"
      )
    }
  }

  private fun sendWarning(context: CommandContext<CommandSourceStack>) {
    val profiles = context
      .getArgument("name", PlayerProfileListResolver::class.java)
      .resolve(context.source)

    val template = WarningNotificationsArgumentType.getArgument("template", context)
    val sender = context.source.sender
    val warning = warningRegistry.getTemplate(template)!!


    profiles.forEach { profile ->
      val uuid = profile.id
      if (uuid != null) {
        val player = plugin.server.getPlayer(uuid)
        if (player != null) {

          if (warning.title != null) {
            player.showTitle(makeTitle("<red>${warning.title}"))
            player.sendRichMessage("<b><red>${warning.title}</red>")
          }

          player.sendRichMessage("<white>${warning.body}</white>")
          player.sendRichMessage("Отправил: <gold>${sender.name}<gold>")

          player.playSound(
            player, Sound.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.UI, 1f, 0.4f
          )

          sender.sendRichMessage("<green>Сообщение успешно отправлено!")
          plugin.slF4JLogger.info("{} sent a warning '{}' to {}", sender.name, warning.name, player.name)
        } else {
          sender.sendRichMessage("<red>Игрок ${profile.name} не в сети!</red>")
        }
      } else {
        sender.sendRichMessage("<red>У профиля ${profile.name} нет UUID!</red>")
      }
    }

  }

  private fun makeTitle(string: String): Title {
    val mm = MiniMessage.miniMessage()
    val component = mm.deserialize(string)
    return Title.title(component, Component.empty())
  }

  private fun sendHelp(sender: CommandSender) {

    sender.sendRichMessage(
      "<b><red>EdenorWarnings</red></b> <gray>(${plugin.pluginMeta.version})</gray> <dark_aqua>- Оповещает игрока предзаписанными сообщениями</dark_aqua>"
    )

    if (sender.hasPermission(HELPER_PERMISSION)) {
      sender.sendRichMessage(
        "<green><hover:show_text:'Нажми, чтобы использовать'><click:suggest_command:/ew list>/ew list</click></hover></green> <yellow>- Вывести список сообщений"
      )
      sender.sendRichMessage(
        "<green><hover:show_text:'Нажми, чтобы использовать'><click:suggest_command:/ew send>/ew send</click></hover> <warning> <username></green> <yellow>- Отправить сообщение игроку"
      )
    }
/*    if (sender.hasPermission(MODERATOR_PERMISSION)) {
      sender.sendRichMessage(
        "Хелп + модер команды"
      )
    }*/
    if (sender.hasPermission(ADMINISTRATOR_PERMISSION)) {
      sender.sendRichMessage(
        "<green><hover:show_text:'Нажми, чтобы использовать'><click:suggest_command:/ew reload>/ew reload</click></hover></green> <yellow>- Перезагрузить настройки"
      )
    }
  }


}