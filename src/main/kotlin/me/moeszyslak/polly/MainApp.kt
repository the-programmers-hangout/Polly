package me.moeszyslak.polly

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import dev.kord.x.emoji.Emojis
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.extensions.pfpUrl
import me.moeszyslak.polly.data.Configuration
import me.moeszyslak.polly.data.MacroStore
import me.moeszyslak.polly.data.Permissions
import me.moeszyslak.polly.services.StatisticsService
import java.awt.Color
import kotlin.time.ExperimentalTime

@PrivilegedIntent
@ExperimentalTime
suspend fun main() {
    val token = System.getenv("BOT_TOKEN") ?: null
    val prefix = System.getenv("DEFAULT_PREFIX") ?: "<none>"
    require(token != null) { "Expected the bot token as an environment variable" }

    bot(token) {
        val configuration = data("config/config.json") { Configuration() }
        val macros = data("config/macros.json") { MacroStore() }

        prefix {
            val configuration = discord.getInjectionObjects(Configuration::class)
            guild?.let { configuration[it.id.value]?.prefix } ?: prefix
        }

        configure {
            allowMentionPrefix = true
            generateCommandDocs = true
            showStartupLog = true
            commandReaction = Emojis.eyes
            theme = Color(0x00BFFF)
            recommendCommands = false
            permissions = Permissions
            intents = Intents.nonPrivileged.plus(Intent.GuildMembers)
        }

        mentionEmbed {
            title = "Polly"
            description = "A simple, elegant macro bot"
            color = it.discord.configuration.theme

            thumbnail {
                url = it.discord.kord.getSelf().pfpUrl
            }

            field {
                name = "Prefix"
                value = it.prefix()
                inline = true
            }

            val statsService = it.discord.getInjectionObjects(StatisticsService::class)
            field {
                name = "Ping"
                value = statsService.ping
                inline = true
            }

            val configuration = it.discord.getInjectionObjects(Configuration::class)
            val guildConfiguration = configuration[it.guild!!.id.value]

            if (guildConfiguration != null) {
                val staffRole = it.guild!!.getRole(Snowflake(guildConfiguration.staffRole))
                val loggingChannel = it.guild!!.getChannel(Snowflake(guildConfiguration.logChannel))
                val cooldown = guildConfiguration.channelCooldown

                field {

                    name = "Configuration"
                    value = "```" +
                            "Staff Role: ${staffRole.name}\n" +
                            "Logging Channel: ${loggingChannel.name}\n" +
                            "Channel Cooldown: $cooldown seconds\n" +
                            "```"
                }
            }

            field {
                val versions = it.discord.versions

                name = "Bot Info"
                value = "```" +
                        "Version: 1.4.1\n" +
                        "DiscordKt: ${versions.library}\n" +
                        "Kord: ${versions.kord}\n" +
                        "Kotlin: ${versions.kotlin}" +
                        "```"
            }

            field {
                name = "Uptime"
                value = statsService.uptime
                inline = true
            }

            field {
                name = "Source"
                value = "[GitHub](https://github.com/the-programmers-hangout/Polly)"
                inline = true
            }
        }

        onStart {
            getInjectionObjects(MacroStore::class).populate()
        }
    }
}