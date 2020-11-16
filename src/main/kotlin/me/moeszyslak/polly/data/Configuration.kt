package me.moeszyslak.polly.data

import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Role
import com.gitlab.kordlib.core.entity.channel.Channel
import me.jakejmattson.discordkt.api.dsl.Data

typealias GuildId = Long

data class Configuration(
        val botOwner: Long = 345541952500006912,
        val guildConfigurations: MutableMap<GuildId, GuildConfiguration> = mutableMapOf()) : Data("config/config.json") {

    operator fun get(id: GuildId) = guildConfigurations[id]
    fun hasGuildConfig(guildId: GuildId) = guildConfigurations.containsKey(guildId)

    fun setup(guildId: GuildId, logChannel: Channel, prefix: String, staffRole: Role, cooldown: Double) {
        if (guildConfigurations[guildId] != null) return

        val newConfiguration = GuildConfiguration(
                logChannel.id.longValue,
                prefix,
                staffRole.id.longValue,
                cooldown,
                mutableSetOf()
        )

        guildConfigurations[guildId] = newConfiguration
        save()
    }
}

data class GuildConfiguration(
        var logChannel: Long,
        var prefix: String,
        var staffRole: Long,
        var channelCooldown: Double,
        var ignoredUsers: MutableSet<Long>
)
