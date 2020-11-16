package me.moeszyslak.polly.data

import me.jakejmattson.discordkt.api.dsl.Data

data class MacroStore(
        val macros: MutableMap<GuildId, MutableMap<String, Macro>> = mutableMapOf()): Data("config/macros.json", killIfGenerated = false) {

    fun<R> forGuild(guildId: GuildId, fn: (MutableMap<String, Macro>) -> R): R {
        val guildMacros = macros.getOrPut(guildId) { mutableMapOf() }
        return fn(guildMacros).also { save() }
    }
}

data class Macro(
        val name: String,
        var contents: String,
        val channel: String?,
        var category: String,
        var uses: Int = 0
)
