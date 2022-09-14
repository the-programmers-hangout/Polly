package me.moeszyslak.polly.data

import dev.kord.core.entity.Role
import dev.kord.core.entity.channel.Channel
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data
import me.jakejmattson.discordkt.dsl.edit

typealias GuildId = ULong

@Serializable
data class Configuration(
    val botOwner: Long = 345541952500006912,
    val guildConfigurations: MutableMap<GuildId, GuildConfiguration> = mutableMapOf()) : Data() {

    operator fun get(id: GuildId) = guildConfigurations[id]
    fun hasGuildConfig(guildId: GuildId) = guildConfigurations.containsKey(guildId)

    fun setup(guildId: GuildId, logChannel: Channel, alertChannel: Channel, cooldown: Double, trackedMacrosEnabled: Boolean) {
        if (guildConfigurations[guildId] != null) return

        val newConfiguration = GuildConfiguration(
            logChannel.id.value,
            alertChannel.id.value,
            trackedMacrosEnabled,
            cooldown,
            mutableSetOf()
        )

        edit { guildConfigurations[guildId] = newConfiguration }
    }
}

@Serializable
data class GuildConfiguration(
    var logChannel: ULong,
    var alertChannel: ULong,
    var trackedMacrosEnabled: Boolean,
    var channelCooldown: Double,
    var ignoredUsers: MutableSet<ULong>,
    var prefix: String = "++"
    )
