package me.moeszyslak.polly.commands

import dev.kord.common.entity.Snowflake
import dev.kord.common.kColor
import dev.kord.core.entity.Member
import me.jakejmattson.discordkt.arguments.ChoiceArg
import me.jakejmattson.discordkt.arguments.UserArg
import me.jakejmattson.discordkt.commands.commands
import me.moeszyslak.polly.data.Configuration
import me.moeszyslak.polly.data.Permissions
import java.awt.Color

fun Member.isIgnored(configuration: Configuration): Boolean {
    val config = configuration[guildId.value] ?: return false

    return config.ignoredUsers.contains(id.value)
}

fun ignoreListCommands(configuration: Configuration) = commands("IgnoreList") {

    command("IgnoreList") {
        description = "Show ignore list."
        requiredPermission = Permissions.STAFF
        execute {
            val config = configuration[guild.id.value] ?: return@execute
            val users = config.ignoredUsers.map { discord.kord.getUser(Snowflake(it))!!.mention }

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

    command("Ignore") {
        description = "Add/remove users from the ignore list."
        requiredPermission = Permissions.STAFF
        execute(
                ChoiceArg("add/remove", "add", "remove"),
                UserArg) {

            val (choice, user) = args
            val config = configuration[guild.id.value] ?: return@execute

            when (choice) {
                "add" -> {
                    if (config.ignoredUsers.contains(user.id.value)) {
                        respond("${user.username} is already being ignored")
                        return@execute
                    }

                    config.ignoredUsers.add(user.id.value)
                    configuration.save()

                    respond("${user.username} added to the ignore list")
                }

                "remove" -> {
                    if (!config.ignoredUsers.contains(user.id.value)) {
                        respond("${user.username} is not being ignored")
                        return@execute
                    }

                    config.ignoredUsers.remove(user.id.value)
                    configuration.save()

                    respond("${user.username} removed from the ignore list")
                }

                else -> {
                    respond("Invalid choice")
                }
            }
        }
    }
}