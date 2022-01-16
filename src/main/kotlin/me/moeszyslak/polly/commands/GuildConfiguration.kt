package me.moeszyslak.polly.commands

import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.ChannelArg
import me.jakejmattson.discordkt.arguments.RoleArg
import me.jakejmattson.discordkt.arguments.TimeArg
import me.jakejmattson.discordkt.commands.commands
import me.moeszyslak.polly.conversations.configurationConversation
import me.moeszyslak.polly.data.Configuration
import me.moeszyslak.polly.data.Permissions
import me.moeszyslak.polly.utilities.timeToString

fun guildConfigurationCommands(configuration: Configuration) = commands("Basics") {

    command("Setup") {
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

    command("Prefix") {
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

    command("StaffRole") {
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

    command("LogChannel") {
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

    command("Cooldown") {
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