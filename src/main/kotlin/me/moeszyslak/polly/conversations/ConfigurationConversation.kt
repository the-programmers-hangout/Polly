package me.moeszyslak.polly.conversations

import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.conversations.conversation
import me.moeszyslak.polly.data.Configuration
import me.moeszyslak.polly.data.GuildId

fun configurationConversation(guildId: GuildId, configuration: Configuration) = conversation {
    val prefix = prompt(EveryArg, "Bot prefix:")
    val log = prompt(ChannelArg, "Log channel:")
    val staffRole = prompt(RoleArg, "Staff role:")
    val cooldown = prompt(TimeArg, "Channel Macro cooldown:")

    configuration.setup(guildId, log, prefix, staffRole, cooldown)
}