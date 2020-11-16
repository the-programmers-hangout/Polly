package me.moeszyslak.polly.commands

import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.ChannelArg
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.arguments.TimeArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.moeszyslak.polly.conversations.configurationConversation
import me.moeszyslak.polly.data.Configuration
import me.moeszyslak.polly.extensions.requiredPermissionLevel
import me.moeszyslak.polly.services.Permission
import me.moeszyslak.polly.utilities.timeToString

fun guildConfigurationCommands(configuration: Configuration) = commands("Basics") {

    guildCommand("Setup") {
        description = "Setup a guild to use Polly"
        requiredPermissionLevel = Permission.GUILD_OWNER
        execute {
            if (configuration.hasGuildConfig(guild.id.longValue)) {
                respond("Guild configuration already exists. You can use commands to modify the config")
                return@execute
            }

            configurationConversation(guild.id.longValue, configuration).startPublicly(discord, author, channel)
            respond("${guild.name} has been setup")
        }
    }

    guildCommand("Prefix") {
        description = "Set the prefix required for the bot to register a command."
        requiredPermissionLevel = Permission.STAFF
        execute(AnyArg("Prefix")) {
            val prefix = args.first
            val config = configuration[guild.id.longValue] ?: return@execute

            config.prefix = prefix
            configuration.save()

            respond("Prefix set to: $prefix")
        }
    }

    guildCommand("StaffRole") {
        description = "Set the role required to use this bot."
        requiredPermissionLevel = Permission.STAFF
        execute(RoleArg) {
            val requiredRole = args.first
            val config = configuration[guild.id.longValue] ?: return@execute

            config.staffRole = requiredRole.id.longValue
            configuration.save()

            respond("Required role set to ${requiredRole.name}")
        }
    }

    guildCommand("LogChannel") {
        description = "Set the channel where logs will be output."
        requiredPermissionLevel = Permission.STAFF
        execute(ChannelArg) {
            val logChannel = args.first
            val config = configuration[guild.id.longValue] ?: return@execute

            config.staffRole = logChannel.id.longValue
            configuration.save()

            respond("Log channel set to ${logChannel.name}")
        }
    }

    guildCommand("Cooldown") {
        description = "Set the cooldown between macro invokes"
        requiredPermissionLevel = Permission.STAFF
        execute(TimeArg) {
            val cooldown = args.first
            val config = configuration[guild.id.longValue] ?: return@execute

            config.channelCooldown = cooldown
            configuration.save()

            respond("Macro cooldown set to ${timeToString(cooldown.toLong() * 1000)}")
        }
    }
}