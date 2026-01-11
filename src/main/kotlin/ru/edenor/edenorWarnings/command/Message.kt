package ru.edenor.edenorWarnings.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.edenor.edenorWarnings.EdenorWarnings
import ru.edenor.edenorWarnings.data.Warning

object Message {

  fun sendWarning(
    warning: Warning,
    sender: CommandSender,
    player: Player,
    plugin: EdenorWarnings
  ) {
    warning.sendTo(player, sender)

    player.playSound(
      player.location,
      Sound.BLOCK_END_PORTAL_FRAME_FILL,
      SoundCategory.PLAYERS,
      1f,
      0.4f
    )

    sender.sendRichMessage("<green>Сообщение успешно отправлено!</green>")
    plugin.logger.info(
      "${sender.name} sent warning '${warning.name}' to ${player.name}"
    )
  }

  private fun Warning.sendTo(player: Player, sender: CommandSender) {


    if (title != null) {
      val titleComponent = Component.text(title, TextColor.color(0xFF3333))
      player.showTitle(Title.title(titleComponent, Component.empty()))
      player.sendRichMessage(title)
    }

    player.sendRichMessage(body)

    player.sendRichMessage("Отправил: <gold>${sender.name}</gold>")
  }
}
