package me.moeszyslak.polly.commands

import com.gitlab.kordlib.common.entity.Snowflake
import me.jakejmattson.discordkt.api.arguments.ChoiceArg
import me.jakejmattson.discordkt.api.arguments.UserArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.moeszyslak.polly.data.Configuration
import me.moeszyslak.polly.extensions.requiredPermissionLevel
import me.moeszyslak.polly.services.Permission
import java.awt.Color

fun ignoreListCommands(configuration: Configuration) = commands("IgnoreList") {

    command("IgnoreList") {
        description = "List users and add/remove users from the ignore list."
        requiredPermissionLevel = Permission.STAFF
        execute(
                ChoiceArg("add/remove/list", "add", "remove", "list")
                        .makeOptional("list"),
                UserArg.makeNullableOptional(null)) {

            val (choice, user) = args
            val config = configuration[guild!!.id.longValue] ?: return@execute

            when (choice) {
                "add" -> {
                    user ?: return@execute respond("Received less arguments than expected. Expected: `(User)`")

                    if (config.ignoredUsers.contains(user.id.longValue))
                        return@execute respond("${user.username} is already being ignored")

                    config.ignoredUsers.add(user.id.longValue)
                    configuration.save()

                    respond("${user.username} added to the ignore list")
                }

                "remove" -> {
                    user ?: return@execute respond("Received less arguments than expected. Expected: `(User)`")

                    if (!config.ignoredUsers.contains(user.id.longValue))
                        return@execute respond("${user.username} is not being ignored")

                    config.ignoredUsers.remove(user.id.longValue)
                    configuration.save()

                    respond("${user.username} removed from the ignore list")
                }

                "list" -> {
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

                else -> {
                    respond("Invalid choice")
                }
            }
        }
    }
}