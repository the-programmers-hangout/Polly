package me.moeszyslak.polly.data

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.GuildMessageChannel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.jakejmattson.discordkt.dsl.Data
import me.jakejmattson.discordkt.dsl.edit

@Serializable
data class MacroStore(
    val macros: MutableMap<Snowflake, MutableMap<String, Macro>> = mutableMapOf()) : Data() {
    @Transient
    var aliases: MutableMap<Snowflake, Map<String, String>> = mutableMapOf()

    fun <R> allAliases(guildId: Snowflake, fn: (Map<String, String>) -> R): R {
        val aliases = aliases[guildId] ?: mutableMapOf()
        return fn(aliases)
    }

    fun <R> findAlias(guildId: Snowflake, alias: String, channel: String, fn: (Macro) -> R): R? {
        return allAliases(guildId) { aliases ->
            val macroString = aliases["$alias#$channel"] ?: return@allAliases null
            val macro = macros[guildId]?.get(macroString) ?: return@allAliases null

            fn(macro)
        }
    }

    fun <R> forGuild(guildId: Snowflake, fn: (MutableMap<String, Macro>) -> R): R {
        val guildMacros = macros.getOrPut(guildId) { mutableMapOf() }
        return fn(guildMacros).also { save(guildId) }
    }

    private fun save(guildId: Snowflake) = edit { populate(guildId) }

    fun populate(guildId: Snowflake? = null) {
        if (guildId == null) {
            macros.forEach { (l, _) -> populate(l) }
            return
        }
        val macros = macros[guildId] ?: return
        val updated = mutableMapOf<String, String>()
        macros.forEach { (key, macro) ->
            updated["${macro.name}#${macro.channel()}"] = key
            macro.aliases.forEach {
                updated["$it#${macro.channel}"] = key
            }
        }

        aliases[guildId] = updated.toMap()
    }
}

@Serializable
data class Macro(
    val name: String,
    val aliases: MutableList<String> = mutableListOf(),
    var contents: String,
    val channel: String,
    var category: String,
    var tracked: Boolean = false,
    var uses: Int = 0
) {
    fun channel() = channel ?: ""

    fun canRun(messageChannel: GuildMessageChannel) = (channel == null || channel == "" || channel == messageChannel.id.toString())

    fun displayNames() =
        listOf(listOf(name), aliases)
            .flatten()
            .joinToString(" | ")
}