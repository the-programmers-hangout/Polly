package me.moeszyslak.polly.services

import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import kotlinx.coroutines.runBlocking
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.discordkt.api.dsl.CommandEvent
import me.jakejmattson.discordkt.api.dsl.listeners
import me.jakejmattson.discordkt.api.extensions.toSnowflakeOrNull
import me.moeszyslak.polly.data.Configuration
import me.moeszyslak.polly.data.Macro
import me.moeszyslak.polly.data.MacroStore

@Service
class MacroService(private val store: MacroStore, private val discord: Discord) {
    fun addMacro(guild: Guild, name: String, category: String, channel: TextChannel?, contents: String): String {
        val channelId = channel?.id?.value ?: ""

        if (name in discord.commands.map { it.names }.flatten()) {
            return "A command with that name already exists"
        }

        val result = store.forGuild(guild) {
            it.putIfAbsent("$name#$channelId", Macro(name, contents, channelId, category))
        }

        return if (result == null) {
            "Success. Macro `$name` is now available ${if (channel == null) "globally" else "on channel ${channel.mention}"} and will respond with ```\n$contents\n```"
        } else {
            "A macro with that name already exists."
        }
    }

    fun removeMacro(guild: Guild, name: String, channel: TextChannel?): String {
        val channelId = channel?.id ?: ""

        val result = store.forGuild(guild) {
            it.remove("$name#$channelId")
        }

        return if (result == null) {
            "Success. Macro `$name` has been removed"
        } else {
            "Cannot find a macro by that name. If it is a channel specific macro you need to provide the channel as well."
        }
    }

    fun editMacro(guild: Guild, name: String, channel: TextChannel?, contents: String): String {
        val channelId = channel?.id ?: ""

        val result = store.forGuild(guild) {
            if (it.containsKey("$name#$channelId")) {
                it["$name#$channelId"]!!.contents = contents
                true
            } else {
                false
            }
        }

        return if (result) {
            "Success. Macro `$name` available ${if (channel == null) "globally" else "on channel ${channel.mention}"} will now respond with ```\n$contents\n```"
        } else {
            "Cannot find a macro by that name. If it is a channel specific macro you need to provide the channel as well."
        }
    }

    fun editMacroCategory(guild: Guild, name: String, channel: TextChannel?, category: String): String {
        val channelId = channel?.id ?: ""

        val result = store.forGuild(guild) {
            if (it.containsKey("$name#$channelId")) {
                it["$name#$channelId"]!!.category = category
                true
            } else {
                false
            }
        }

        return if (result) {
            "Success. Macro `$name` available ${if (channel == null) "globally" else "on channel ${channel.mention}"} is now in category `${category}`"
        } else {
            "Cannot find a macro by that name. If it is a channel specific macro you need to provide the channel as well."
        }
    }

    suspend fun listMacros(event: CommandEvent<*>, guild: Guild, channel: TextChannel) = with(event) {
        val availableMacros = getMacrosAvailableIn(guild, channel)
                .groupBy { it.category }
                .toList()
                .sortedByDescending { it.second.size }

        val chunks = availableMacros.chunked(25)

        event.respondMenu {
            chunks.map {
                page {
                    title = "Macros available in ${channel.name}"
                    color = discord.configuration.theme

                    if (it.isNotEmpty()) {
                        it.map { (category, macros) ->
                            val sorted = macros.sortedBy { it.name }

                            field {
                                name = "**$category**"
                                value = "```css\n${sorted.joinToString("\n") { it.name }}\n```"
                                inline = true
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun listAllMacros(event: CommandEvent<*>, guild: Guild) {
        val allMacros = store.forGuild(guild) { it }
                .map { it.value }
                .groupBy { it.channel?.toSnowflakeOrNull()?.let { guild.getChannel(it).name } ?: "Global Macros" }
                .toList()
                .sortedByDescending { it.second.size }

        val chunks = allMacros.chunked(25)

        event.respondMenu {
            chunks.map {
                page {
                    title = "All available macros"
                    color = event.discord.configuration.theme

                    if (it.isNotEmpty()) {
                        it.map { (channel, macros) ->
                            field {
                                name = "**$channel**"
                                value = "```css\n${macros.joinToString("\n") { it.name }}\n```"
                                inline = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getMacrosAvailableIn(guild: Guild, channel: TextChannel): List<Macro> {
        val macroList = store.forGuild(guild) { macros ->
            macros.filter {
                it.key.endsWith('#') || it.key.takeLast(18) == channel.id.value
            }
        }

        return macroList.filterKeys { key ->
            if (key.endsWith('#')) {
                macroList.keys.none { it.startsWith(key) && !it.endsWith('#') }
            } else {
                true
            }
        }.map { it.value }
    }

    fun findMacro(guild: Guild, name: String, channel: MessageChannelBehavior): Macro? {
        return store.forGuild(guild) {
            // first try to find a channel specific macro
            // if it fails, default to a global macro
            it["$name#${channel.id}"] ?: it["$name#"]
        }
    }
}

fun macroListener(macroService: MacroService, configuration: Configuration) = listeners {
    on<MessageCreateEvent> {
        val guild = runBlocking {
            getGuild()
        } ?: return@on

        val prefix = configuration[guild.id.longValue]?.prefix

        if (prefix.isNullOrEmpty()) {
            return@on
        }

        if (!message.content.startsWith(prefix)) {
            return@on
        }

        val macroName = message.content
                .split(prefix, limit = 2)
                .getOrNull(1)
                ?.split("\\s".toRegex(), limit = 1)
                ?.firstOrNull()
                ?: return@on


        val macro = macroService.findMacro(guild, macroName, message.channel)

        if (macro != null) {
            runBlocking {
                message.channel.createMessage(macro.contents)
            }
//            message.delete()

        }
    }
}
