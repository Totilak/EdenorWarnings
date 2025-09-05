package ru.edenor.edenorWarnings.api

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import ru.edenor.edenorWarnings.EdenorWarnings
import ru.edenor.edenorWarnings.EdenorWarnings.Companion.NOTIFY_PERMISSION

object WarningAPI {

  fun issueViolation(player: Player, reason: String) {
    val title = "<red>Вам выдано предупреждение!</red>"
    val text = "<red>[<dark_red>EdenorFilterChat</dark_red>]</red> $reason"
    player.showTitle(makeTitle(title))
    player.sendRichMessage(text)
    player.playSound(
      player, Sound.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.UI, 1f, 0.4f
    )

    Bukkit.getOnlinePlayers().forEach { online ->
      if (online.hasPermission(NOTIFY_PERMISSION)) {
        online.sendRichMessage("<red>[<dark_red>EdenorFilterChat</dark_red>]</red> <yellow>${player.name}</yellow> получил предупреждение $reason")
      }
    }

    EdenorWarnings.instance.slF4JLogger.info("EdenorChatFilter sent a warning  to {}", player.name)

  }

  private fun makeTitle(string: String): Title {
    val mm = MiniMessage.miniMessage()
    val component = mm.deserialize(string)
    return Title.title(component, Component.empty())
  }

}