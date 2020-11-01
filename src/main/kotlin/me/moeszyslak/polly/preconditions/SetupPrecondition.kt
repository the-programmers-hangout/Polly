package me.moeszyslak.polly.preconditions

import me.jakejmattson.discordkt.api.dsl.*
import me.moeszyslak.polly.data.Configuration

fun setupPrecondition(configuration: Configuration) = precondition {
    val command: Command = command ?: return@precondition fail()
    val guild = guild ?: return@precondition fail()

    if (!author.asMember(guild.id).isOwner())
        fail()

    if (!command.names.contains("Setup")) {
        if (!configuration.hasGuildConfig(guild.id.longValue))
            fail("You must first use the `Setup` command in this guild.")
    }
}
