package ru.edenor.edenorWarnings.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.MessageComponentSerializer
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import net.kyori.adventure.text.Component
import ru.edenor.edenorWarnings.data.WarningRegistry
import java.util.concurrent.CompletableFuture

class WarningNotificationsArgumentType(
  private val warningRegistry: WarningRegistry
) : CustomArgumentType<String, String> {

  companion object {
    @JvmStatic
    val ERROR_BAD_SOURCE = SimpleCommandExceptionType(
      MessageComponentSerializer.message()
        .serialize(Component.text("The source needs to be a CommandSourceStack!"))
    )

    @JvmStatic
    val ERROR_WARNING_NOT_FOUND = DynamicCommandExceptionType {
      MessageComponentSerializer.message()
        .serialize(Component.text("Warning '$it' не найден!"))
    }

    fun getArgument(argument: String, context: CommandContext<CommandSourceStack>): String =
      context.getArgument(argument, String::class.java)
  }

  override fun parse(reader: StringReader): String = reader.readUnquotedString()

  override fun getNativeType(): ArgumentType<String> = StringArgumentType.word()

  override fun <S : Any> parse(reader: StringReader, source: S): String {
    if (source !is CommandSourceStack) {
      throw ERROR_BAD_SOURCE.create()
    }
    val warningName = nativeType.parse(reader)
    val warning = warningRegistry.getTemplate(warningName)

    if (warning == null || (warning.permission != null && !source.sender.hasPermission(warning.permission))) {
      throw ERROR_WARNING_NOT_FOUND.create(warningName)
    }

    return warningName
  }

  override fun <S : Any> listSuggestions(
    context: CommandContext<S>,
    builder: SuggestionsBuilder
  ): CompletableFuture<Suggestions> {
    val source = context.source as? CommandSourceStack
    warningRegistry.getTemplates()
      .filter { it.permission?.let { p -> source?.sender?.hasPermission(p) ?: true } ?: true }
      .map { it.name }
      .filter { it.startsWith(builder.remaining, ignoreCase = true) }
      .forEach { builder.suggest(it) }

    return builder.buildFuture()
  }
}
