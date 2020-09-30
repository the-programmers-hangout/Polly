package me.moeszyslak.macaroni.preconditions

import me.jakejmattson.discordkt.api.dsl.*
import me.moeszyslak.macaroni.data.Configuration


class SetupPrecondition(private val configuration: Configuration) : Precondition() {
    override suspend fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val command = event.command ?: return Fail()
        val guild = event.guild!!

        if (!event.author.asMember(event.guild!!.id).isOwner())
            return Fail()

        if (!command.names.contains("Setup")) {
            if (!configuration.hasGuildConfig(guild.id.longValue))
                return Fail("You must first use the `Setup` command in this guild.")
        }

        return Pass
    }
}