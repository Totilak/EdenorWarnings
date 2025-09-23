package ru.edenor.edenorWarnings.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import ru.edenor.edenorWarnings.EdenorWarnings.Companion.ADMINISTRATOR_PERMISSION
import ru.edenor.edenorWarnings.EdenorWarnings.Companion.HELPER_PERMISSION
import ru.edenor.edenorWarnings.EdenorWarnings.Companion.MODERATOR_PERMISSION

object CommandExtensions {
  internal fun LiteralArgumentBuilder<CommandSourceStack>.requiresPermission(
    permission: String
  ) = this.requires { it.sender.hasPermission(permission) }


  internal fun LiteralArgumentBuilder<CommandSourceStack>.requiresAnyPermissionOf(
    vararg permissions: String
  ) = this.requires { ctx -> permissions.any { ctx.sender.hasPermission(it) } }

  internal fun LiteralArgumentBuilder<CommandSourceStack>.requiresAnyPermission() =
    this.requiresAnyPermissionOf(
      HELPER_PERMISSION,
      MODERATOR_PERMISSION,
      ADMINISTRATOR_PERMISSION
    )


  internal fun LiteralArgumentBuilder<CommandSourceStack>.simplyRun(
    block: (CommandSender) -> Unit
  ) =
    this.executes {
      block.invoke(it.source.sender)
      Command.SINGLE_SUCCESS
    }

  internal fun <S, T> RequiredArgumentBuilder<S, T>.simplyRun(
    block: (CommandContext<S>) -> Unit
  ) =
    this.executes {
      block.invoke(it)
      Command.SINGLE_SUCCESS
    }

}