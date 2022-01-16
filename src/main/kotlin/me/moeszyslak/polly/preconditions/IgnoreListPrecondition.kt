package me.moeszyslak.polly.preconditions

import me.jakejmattson.discordkt.dsl.*
import me.moeszyslak.polly.commands.isIgnored
import me.moeszyslak.polly.data.Configuration

fun ignoreListPrecondition(configuration: Configuration) = precondition {
    val guild = guild ?: return@precondition fail()
    val member = author.asMember(guild.id)
    if (member.isIgnored(configuration)) {
        fail()
    }
}
