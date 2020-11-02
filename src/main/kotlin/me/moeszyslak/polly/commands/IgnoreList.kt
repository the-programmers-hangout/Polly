package me.moeszyslak.polly.commands

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.Member
import me.jakejmattson.discordkt.api.arguments.ChoiceArg
import me.jakejmattson.discordkt.api.arguments.UserArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.moeszyslak.polly.data.Configuration
import me.moeszyslak.polly.extensions.requiredPermissionLevel
import me.moeszyslak.polly.services.Permission
import java.awt.Color

fun Member.isIgnored(configuration: Configuration): Boolean {
    val config = configuration[guildId.longValue] ?: return false

    return config.ignoredUsers.contains(id.longValue)
}

fun ignoreListCommands(configuration: Configuration) = commands("IgnoreList") {

    guildCommand("IgnoreList") {
        description = "Show ignore list."
        requiredPermissionLevel = Permission.STAFF
        execute {
            val config = configuration[guild.id.longValue] ?: return@execute
            val users = config.ignoredUsers.map { discord.api.getUser(Snowflake(it))!!.mention }

            respond {
                title = "Ignored users"

                if (config.ignoredUsers.isEmpty()) {
                    color = Color(0xE10015)
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

    guildCommand("Ignore") {
        description = "Add/remove users from the ignore list."
        requiredPermissionLevel = Permission.STAFF
        execute(
                ChoiceArg("add/remove/list", "add", "remove"),
                UserArg) {

            val (choice, user) = args
            val config = configuration[guild.id.longValue] ?: return@execute

            when (choice) {
                "add" -> {
                    if (config.ignoredUsers.contains(user.id.longValue)) {
                        respond("${user.username} is already being ignored")
                        return@execute
                    }

                    config.ignoredUsers.add(user.id.longValue)
                    configuration.save()

                    respond("${user.username} added to the ignore list")
                }

                "remove" -> {
                    if (!config.ignoredUsers.contains(user.id.longValue)) {
                        respond("${user.username} is not being ignored")
                        return@execute
                    }

                    config.ignoredUsers.remove(user.id.longValue)
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