package me.moeszyslak.polly.data

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.Channel
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data
import me.jakejmattson.discordkt.dsl.edit

@Serializable
data class Configuration(val guildConfigurations: MutableMap<Snowflake, GuildConfiguration> = mutableMapOf()) : Data() {

    operator fun get(id: Snowflake) = guildConfigurations[id]
    fun hasGuildConfig(guildId: Snowflake) = guildConfigurations.containsKey(guildId)

    fun setup(guildId: Snowflake, logChannel: Channel, alertChannel: Channel, cooldown: Double, trackedMacrosEnabled: Boolean) {
        if (guildConfigurations[guildId] != null) return

        edit { guildConfigurations[guildId] = GuildConfiguration(logChannel.id, alertChannel.id, trackedMacrosEnabled, cooldown) }
    }
}

@Serializable
data class GuildConfiguration(
    var logChannel: Snowflake,
    var alertChannel: Snowflake,
    var trackedMacrosEnabled: Boolean,
    var channelCooldown: Double,
    var ignoredUsers: MutableSet<Snowflake> = mutableSetOf(),
    var prefix: String = "+"
)
