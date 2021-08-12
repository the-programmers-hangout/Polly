package me.moeszyslak.polly.commands

import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.ChannelArg
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.arguments.TimeArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.moeszyslak.polly.conversations.configurationConversation
import me.moeszyslak.polly.data.Configuration
import me.moeszyslak.polly.data.Permissions
import me.moeszyslak.polly.utilities.timeToString

fun guildConfigurationCommands(configuration: Configuration) = commands("Basics") {

    guildCommand("Setup") {
        description = "Setup a guild to use Polly"
        requiredPermission = Permissions.GUILD_OWNER
        execute {
            if (configuration.hasGuildConfig(guild.id.value)) {
                respond("Guild configuration already exists. You can use commands to modify the config")
                return@execute
            }

            configurationConversation(guild.id.value, configuration).startPublicly(discord, author, channel)
            respond("${guild.name} has been setup")
        }
    }

    guildCommand("Prefix") {
        description = "Set the prefix required for the bot to register a command."
        requiredPermission = Permissions.STAFF
        execute(AnyArg("Prefix")) {
            val prefix = args.first
            val config = configuration[guild.id.value] ?: return@execute

            config.prefix = prefix
            configuration.save()

            respond("Prefix set to: $prefix")
        }
    }

    guildCommand("StaffRole") {
        description = "Set the role required to use this bot."
        requiredPermission = Permissions.STAFF
        execute(RoleArg) {
            val requiredRole = args.first
            val config = configuration[guild.id.value] ?: return@execute

            config.staffRole = requiredRole.id.value
            configuration.save()

            respond("Required role set to ${requiredRole.name}")
        }
    }

    guildCommand("LogChannel") {
        description = "Set the channel where logs will be output."
        requiredPermission = Permissions.STAFF
        execute(ChannelArg) {
            val logChannel = args.first
            val config = configuration[guild.id.value] ?: return@execute

            config.staffRole = logChannel.id.value
            configuration.save()

            respond("Log channel set to ${logChannel.name}")
        }
    }

    guildCommand("Cooldown") {
        description = "Set the cooldown between macro invokes"
        requiredPermission = Permissions.STAFF
        execute(TimeArg) {
            val cooldown = args.first
            val config = configuration[guild.id.value] ?: return@execute

            config.channelCooldown = cooldown
            configuration.save()

            respond("Macro cooldown set to ${timeToString(cooldown.toLong() * 1000)}")
        }
    }
}