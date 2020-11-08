package me.moeszyslak.polly

import com.gitlab.kordlib.gateway.Intent
import com.gitlab.kordlib.gateway.Intents
import com.gitlab.kordlib.gateway.PrivilegedIntent
import com.gitlab.kordlib.kordx.emoji.Emojis
import me.jakejmattson.discordkt.api.dsl.bot
import me.jakejmattson.discordkt.api.extensions.toSnowflake
import me.moeszyslak.polly.data.Configuration
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
        prefix {
            val configuration = discord.getInjectionObjects(Configuration::class)
            guild?.let { configuration[it.id.longValue]?.prefix } ?: prefix
        }

        configure {
            allowMentionPrefix = true
            generateCommandDocs = true
            showStartupLog = true
            commandReaction = Emojis.eyes
            theme = Color(0x00BFFF)
        }

        mentionEmbed {
            title = "Polly"
            description = "A simple, elegant macro bot"
            color = it.discord.configuration.theme

            thumbnail {
                url = it.discord.api.getSelf().avatar.url
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
            val guildConfiguration = configuration[it.guild!!.id.longValue]

            if (guildConfiguration != null) {
                val staffRole = it.guild!!.getRole(guildConfiguration.staffRole.toSnowflake())
                val loggingChannel = it.guild!!.getChannel(guildConfiguration.logChannel.toSnowflake())

                field {

                    name = "Configuration"
                    value = "```" +
                            "Staff Role: ${staffRole.name}\n" +
                            "Logging Channel: ${loggingChannel.name}\n" +
                            "```"
                }
            }

            field {
                val versions = it.discord.versions

                name = "Bot Info"
                value = "```" +
                        "Version: 1.0.0\n" +
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

        intents {
            Intents.nonPrivileged.intents.forEach {
                +it
            }

            +Intent.GuildMembers
        }

        permissions {
            true
        }
    }
}