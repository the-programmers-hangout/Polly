package me.moeszyslak.polly.listeners

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.toReaction
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.jakejmattson.discordkt.dsl.listeners
import me.jakejmattson.discordkt.extensions.jumpLink
import me.jakejmattson.discordkt.extensions.pfpUrl
import me.moeszyslak.polly.commands.isIgnored
import me.moeszyslak.polly.data.Configuration
import me.moeszyslak.polly.data.Macro
import me.moeszyslak.polly.services.MacroService

val macroCooldown = mutableListOf<Pair<Snowflake, Macro>>()

fun macroListener(macroService: MacroService, configuration: Configuration) = listeners {
    on<MessageCreateEvent> {
        val guild = getGuild() ?: return@on
        val guildId = guild.id
        val member = member ?: return@on
        if (member.isIgnored(configuration)) {
            return@on
        }

        val guildConfiguration = configuration[guildId] ?: return@on
        val prefix = guildConfiguration.prefix

        if (!message.content.startsWith(prefix)) {
            return@on
        }

        val macroName = message.content
            .replace(prefix, "")
            .split("\\s".toRegex(), limit = 2)
            .firstOrNull()
            ?: return@on

        val macro = macroService.findMacro(guildId, macroName, message.channel) ?: return@on

        if (macroCooldown.contains(message.channelId to macro)) {
            message.addReaction(Emojis.clock4.toReaction())
            return@on
        }

        macroCooldown += message.channelId to macro

        GlobalScope.launch {
            val cooldown = guildConfiguration.channelCooldown * 1000
            delay(cooldown.toLong())
            macroCooldown -= message.channelId to macro
        }

        if (message.content.startsWith("$prefix$prefix")) {
            message.addReaction(Emojis.eyes.toReaction())
        } else {
            message.delete()
        }

        val macroMessage = message.channel.createMessage(macro.contents)

        val logChannelId = configuration[guildId]?.logChannel ?: return@on

        guild.getChannelOf<GuildMessageChannel>(logChannelId)
            .createMessage("${member.username} :: ${member.id.value} " +
                "invoked $macroName in ${message.channel.mention}")

        if (guildConfiguration.trackedMacrosEnabled && macro.tracked) {
            val alertChannelId = configuration[guildId]?.alertChannel ?: return@on

            guild.getChannelOf<GuildMessageChannel>(alertChannelId)
                .createEmbed {
                    title = "Tracked Macro Invoked"
                    color = discord.configuration.theme
                    description = """
                        **${macro.name}** invoked in ${message.channel.mention}. 
                        Staff action may be needed [here](${macroMessage.jumpLink()}).
                    """.trimIndent()
                    footer {
                        icon = member.pfpUrl
                        text = "Invoked by ${member.tag} :: ${member.id.value}"
                    }
                }
        }
    }
}