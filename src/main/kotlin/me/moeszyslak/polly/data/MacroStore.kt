package me.moeszyslak.polly.data

import com.gitlab.kordlib.core.entity.channel.TextChannel
import me.jakejmattson.discordkt.api.dsl.Data

data class MacroStore(
        val macros: MutableMap<GuildId, MutableMap<String, Macro>> = mutableMapOf()) : Data("config/macros.json", killIfGenerated = false) {
    @Transient
    val aliases: MutableMap<GuildId, MutableMap<String, Macro>> = mutableMapOf()

    fun <R> withAliases(guildId: GuildId, fn: (MutableMap<String, Macro>) -> R): R {
        val aliases = aliases[guildId] ?: mutableMapOf()
        return fn(aliases)
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
        aliases[guildId] = mutableMapOf()
        macros.forEach { (_, macro) ->
            aliases[guildId]!!["${macro.name}#${macro.channel()}"] = macro.copy(parent = macro.name)
            macro.aliases.forEach {
                aliases[guildId]!!["${it}#${macro.channel}"] = macro.copy(name = it, parent = macro.name)
            }
        }
    }
}

data class Macro(
        val name: String,
        var parent: String = "",
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
                    .joinToString(", ") { "`$it`" }
}

fun newMacro(name: String, contents: String, channel: String, category: String): Macro {
    return Macro(name, "", mutableListOf(), contents, channel, category)
}
