package me.moeszyslak.polly.data

import dev.kord.core.entity.channel.GuildMessageChannel
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data

@Serializable
data class MacroStore(
        val macros: MutableMap<GuildId, MutableMap<String, Macro>> = mutableMapOf()) : Data() {
    @Transient
    var aliases: MutableMap<GuildId, Map<String, String>> = mutableMapOf()

    fun <R> allAliases(guildId: GuildId, fn: (Map<String, String>) -> R): R {
        val aliases = aliases[guildId] ?: mutableMapOf()
        return fn(aliases)
    }

    fun <R> findAlias(guildId: GuildId, alias: String, channel: String, fn: (Macro) -> R): R? {
        return allAliases(guildId) { aliases ->
            val macroString = aliases["$alias#$channel"] ?: return@allAliases null
            val macro = macros[guildId]?.get(macroString) ?: return@allAliases null

            fn(macro)
        }
    }

    fun <R> forGuild(guildId: GuildId, fn: (MutableMap<String, Macro>) -> R): R {
        val guildMacros = macros.getOrPut(guildId) { mutableMapOf() }
        return fn(guildMacros).also { save(guildId) }
    }

    private fun save(guildId: GuildId) {
        populate(guildId)
        save()
    }

    fun populate(guildId: GuildId? = null) {
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
        var aliases: MutableList<String> = mutableListOf(),
        var contents: String,
        val channel: String?,
        var category: String,
        var tracked: Boolean = false,
        var uses: Int = 0
) {
    fun channel() = channel ?: ""

    fun canRun(messageChannel: GuildMessageChannel) =
            (channel == null || channel == "" || channel == messageChannel.id.asString)

    fun displayNames() =
            listOf(listOf(name), aliases)
                    .flatten()
                    .joinToString(" | ")
}

fun newMacro(name: String, contents: String, channel: String, category: String, tracked: Boolean = false): Macro {
    return Macro(name, mutableListOf(), contents, channel, category, tracked)
}