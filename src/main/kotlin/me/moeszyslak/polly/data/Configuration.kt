package me.moeszyslak.polly.data

import dev.kord.core.entity.Role
import dev.kord.core.entity.channel.Channel
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data

typealias GuildId = ULong

@Serializable
data class Configuration(
        val botOwner: Long = 345541952500006912,
        val guildConfigurations: MutableMap<GuildId, GuildConfiguration> = mutableMapOf()) : Data() {

    operator fun get(id: GuildId) = guildConfigurations[id]
    fun hasGuildConfig(guildId: GuildId) = guildConfigurations.containsKey(guildId)

    fun setup(guildId: GuildId, logChannel: Channel, alertChannel: Channel, prefix: String, staffRole: Role, cooldown: Double) {
        if (guildConfigurations[guildId] != null) return

        val newConfiguration = GuildConfiguration(
            logChannel.id.value,
            alertChannel.id.value,
            true,
            prefix,
            staffRole.id.value,
            cooldown,
            mutableSetOf()
        )

        guildConfigurations[guildId] = newConfiguration
        save()
    }
}

@Serializable
data class GuildConfiguration(
        var logChannel: ULong,
        var alertChannel: ULong,
        var trackedMacrosEnabled: Boolean,
        var prefix: String,
        var staffRole: ULong,
        var channelCooldown: Double,
        var ignoredUsers: MutableSet<ULong>
)
