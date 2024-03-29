package me.moeszyslak.polly.commands

import dev.kord.common.kColor
import dev.kord.core.entity.Member
import me.jakejmattson.discordkt.arguments.ChoiceArg
import me.jakejmattson.discordkt.arguments.UserArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.dsl.edit
import me.moeszyslak.polly.data.Configuration
import java.awt.Color

fun Member.isIgnored(configuration: Configuration): Boolean {
    val config = configuration[guildId] ?: return false

    return config.ignoredUsers.contains(id)
}

fun ignoreListCommands(configuration: Configuration) = commands("IgnoreList") {
    slash("IgnoreList", "Show ignore list.") {
        execute {
            val config = configuration[guild.id] ?: return@execute
            val users = config.ignoredUsers.map { discord.kord.getUser(it)!!.mention }

            respond {
                title = "Ignored users"

                if (config.ignoredUsers.isEmpty()) {
                    color = Color(0xE10015).kColor
                    field {
                        value = "There are currently no ignored users."
                    }
                } else {
                    field {
                        value = users.joinToString()
                    }
                }
            }
        }
    }

    slash("Ignore", "Add/remove users from the ignore list.") {
        execute(
            ChoiceArg("option", "add", "remove"),
            UserArg) {

            val (choice, user) = args
            val config = configuration[guild.id] ?: return@execute

            when (choice) {
                "add" -> {
                    if (config.ignoredUsers.contains(user.id)) {
                        respond("${user.username} is already being ignored")
                        return@execute
                    }

                    configuration.edit { config.ignoredUsers.add(user.id) }
                    respond("${user.username} added to the ignore list")
                }

                "remove" -> {
                    if (!config.ignoredUsers.contains(user.id)) {
                        respond("${user.username} is not being ignored")
                        return@execute
                    }

                    configuration.edit { config.ignoredUsers.remove(user.id) }
                    respond("${user.username} removed from the ignore list")
                }

                else -> {
                    respond("Invalid choice")
                }
            }
        }
    }
}