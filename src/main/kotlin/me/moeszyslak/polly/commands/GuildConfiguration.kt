package me.moeszyslak.polly.commands

import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.edit
import me.moeszyslak.polly.data.Configuration
import me.moeszyslak.polly.utilities.timeToString

fun guildConfigurationCommands(configuration: Configuration) = commands("Basics") {
    slash("Setup") {
        execute(ChannelArg("LogChannel"), ChannelArg("AlertChannel"), TimeArg("Cooldown"), BooleanArg("TrackedMacros")) {
            val (logChannel, alertChannel, cooldown, trackedMacros) = args
            configuration.setup(guild.id, logChannel, alertChannel, cooldown, trackedMacros)
            respondPublic("${guild.name} has been setup")
        }
    }

    slash("LogChannel", "Set the channel where logs will be output.") {
        execute(ChannelArg) {
            val logChannel = args.first
            val config = configuration[guild.id] ?: return@execute

            configuration.edit { config.logChannel = logChannel.id }
            respond("Log channel set to ${logChannel.name}")
        }
    }

    slash("AlertChannel", "Set the channel where alerts will be output.") {
        execute(ChannelArg) {
            val alertChannel = args.first
            val config = configuration[guild.id] ?: return@execute

            configuration.edit { config.alertChannel = alertChannel.id }
            respond("Alert channel set to ${alertChannel.name}")
        }
    }

    slash("Prefix", "Set the prefix required legacy macro invocations.") {
        execute(AnyArg("Prefix")) {
            val prefix = args.first
            val config = configuration[guild.id] ?: return@execute

            configuration.edit { config.prefix = prefix }
            respond("Prefix set to: $prefix")
        }
    }

    slash("Cooldown", "Set the cooldown between macro invokes") {
        execute(TimeArg) {
            val cooldown = args.first
            val config = configuration[guild.id] ?: return@execute

            configuration.edit { config.channelCooldown = cooldown }
            respond("Macro cooldown set to ${timeToString(cooldown.toLong() * 1000)}")
        }
    }

    slash("TrackedMacros", "Toggle tracked macros (macros that post to the alert channel)") {
        execute(BooleanArg("Enabled", "enable", "disable")) {
            val enabled = args.first
            val config = configuration[guild.id] ?: return@execute

            configuration.edit { config.trackedMacrosEnabled = enabled }
            respond("Logging of tracked macros is now ${if (enabled) "**enabled**" else "**disabled**"}")
        }
    }
}