package ru.edenor.edenorWarnings.command

import com.destroystokyo.paper.profile.PlayerProfile
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.command.CommandSender
import ru.edenor.edenorWarnings.EdenorWarnings
import ru.edenor.edenorWarnings.data.Warning

object Message {
  private val mm = MiniMessage.miniMessage()

  fun sendWarning(
    warning: Warning,
    sender: CommandSender,
    profile: PlayerProfile,
    plugin: EdenorWarnings
  ) {
    val uuid = profile.id ?: profile.noUuid(sender)
    val player = plugin.server.getPlayer(uuid) ?: profile.offline(sender)

    warning.sendTo(player, sender)
    player.playSound(player.location, Sound.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.MASTER, 1f, 0.4f)
    sender.sendRichMessage("<green>Сообщение успешно отправлено!")
    plugin.slF4JLogger.info("{} sent a warning '{}' to {}", sender.name, warning.name, player.name)
  }


  private fun PlayerProfile.noUuid(sender: CommandSender): Nothing {
    sender.sendRichMessage("<red>У профиля $name нет UUID!</red>")
    throw IllegalStateException()
  }

  private fun PlayerProfile.offline(sender: CommandSender): Nothing {
    sender.sendRichMessage("<red>Игрок $name не в сети!</red>")
    throw IllegalStateException()
  }

  private fun Warning.sendTo(player: org.bukkit.entity.Player, sender: CommandSender) {
    title?.let {
      player.showTitle(makeTitle("<red>$it"))
      player.sendRichMessage("<b><red>$it</red>")
    }

    player.sendRichMessage("<white>$body</white>")
    player.sendRichMessage("Отправил: <gold>${sender.name}</gold>")
  }

  private fun makeTitle(string: String): Title {
    val component = mm.deserialize(string)
    return Title.title(component, Component.empty())
  }

}