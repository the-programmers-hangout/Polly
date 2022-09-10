package me.moeszyslak.polly.commands

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.edit
import me.moeszyslak.polly.conversations.configurationConversation
import me.moeszyslak.polly.data.Configuration
import me.moeszyslak.polly.utilities.timeToString

fun guildConfigurationCommands(configuration: Configuration) = commands("Basics") {
    text("Setup") {
        description = "Setup a guild to use Polly"
        requiredPermissions = Permissions(Permission.ManageGuild)
        execute {
            if (configuration.hasGuildConfig(guild.id.value)) {
                respond("Guild configuration already exists. You can use commands to modify the config")
                return@execute
            }

            configurationConversation(guild.id.value, configuration).startPublicly(discord, author, channel)
            respond("${guild.name} has been setup")
        }
    }

    text("Prefix") {
        description = "Set the prefix required for the bot to register a command."
        execute(AnyArg("Prefix")) {
            val prefix = args.first
            val config = configuration[guild.id.value] ?: return@execute

            configuration.edit { config.prefix = prefix }
            respond("Prefix set to: $prefix")
        }
    }

    text("StaffRole") {
        description = "Set the role required to use this bot."
        execute(RoleArg) {
            val requiredRole = args.first
            val config = configuration[guild.id.value] ?: return@execute

            configuration.edit { config.staffRole = requiredRole.id.value }
            respond("Required role set to ${requiredRole.name}")
        }
    }

    text("LogChannel") {
        description = "Set the channel where logs will be output."
        execute(ChannelArg) {
            val logChannel = args.first
            val config = configuration[guild.id.value] ?: return@execute

            configuration.edit { config.logChannel = logChannel.id.value }
            respond("Log channel set to ${logChannel.name}")
        }
    }

    text("AlertChannel") {
        description = "Set the channel where alerts will be output."
        execute(ChannelArg) {
            val alertChannel = args.first
            val config = configuration[guild.id.value] ?: return@execute

            configuration.edit { config.alertChannel = alertChannel.id.value }
            respond("Alert channel set to ${alertChannel.name}")
        }
    }

    text("Cooldown") {
        description = "Set the cooldown between macro invokes"
        execute(TimeArg) {
            val cooldown = args.first
            val config = configuration[guild.id.value] ?: return@execute

            configuration.edit { config.channelCooldown = cooldown }
            respond("Macro cooldown set to ${timeToString(cooldown.toLong() * 1000)}")
        }
    }

    text("TrackedMacros") {
        description = "Toggle tracked macros (macros that post to the alert channel)"
        execute(BooleanArg("Enabled", "enable", "disable")) {
            val enabled = args.first
            val config = configuration[guild.id.value] ?: return@execute

            configuration.edit { config.trackedMacrosEnabled = enabled }
            respond("Logging of tracked macros is now ${if (enabled) "**enabled**" else "**disabled**"}")
        }
    }
}