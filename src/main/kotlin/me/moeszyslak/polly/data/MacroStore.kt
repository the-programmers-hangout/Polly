package me.moeszyslak.polly.data

import com.gitlab.kordlib.core.entity.channel.TextChannel
import me.jakejmattson.discordkt.api.dsl.Data

data class MacroStore(
        val macros: MutableMap<GuildId, MutableMap<String, Macro>> = mutableMapOf()) : Data("config/macros.json", killIfGenerated = false) {
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

data class Macro(
        val name: String,
        var aliases: MutableList<String> = mutableListOf(),
        var contents: String,
        val channel: String?,
        var category: String,
        var uses: Int = 0
) {
    fun channel() = channel ?: ""

    fun canRun(textChannel: TextChannel) =
            (channel == null || channel == "" || channel == textChannel.id.value)

    fun displayNames() =
            listOf(listOf(name), aliases)
                    .flatten()
                    .joinToString(" | ")
}

fun newMacro(name: String, contents: String, channel: String, category: String): Macro {
    return Macro(name, mutableListOf(), contents, channel, category)
}