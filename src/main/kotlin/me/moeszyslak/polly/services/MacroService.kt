package me.moeszyslak.polly.services

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.toReaction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.commands.CommandEvent
import me.jakejmattson.discordkt.commands.GuildCommandEvent
import me.jakejmattson.discordkt.dsl.listeners
import me.jakejmattson.discordkt.extensions.jumpLink
import me.jakejmattson.discordkt.extensions.pfpUrl
import me.jakejmattson.discordkt.extensions.toSnowflakeOrNull
import me.moeszyslak.polly.commands.isIgnored
import me.moeszyslak.polly.data.*
import me.xdrop.fuzzywuzzy.FuzzySearch


@Service
class MacroService(private val store: MacroStore, private val discord: Discord, private val configuration: Configuration) {
    private val allCommands
        get() = discord.commands.map { it.names }.flatten().map { it.toLowerCase() }

    suspend fun macroInfo(event: GuildCommandEvent<*>, guild: GuildId, name_raw: String, channel: GuildMessageChannel?) {
        val channelId = channel?.id?.asString ?: ""
        val name = name_raw.toLowerCase()

        val parent = store.findAlias(guild, name, channelId) { it } ?: run {
            event.respond("Cannot find a macro by that name. If it is a channel specific macro you need to provide the channel as well.")
            return
        }

        event.respond {
            title = "Macro - $name"
            color = discord.configuration.theme
            description = "```${parent.contents}```"
            field {
                this.name = "Macro Name"
                value = "```fix\n${parent.name}```"
                inline = true
            }
            field {
                this.name = "Aliases"
                value = if (parent.aliases.isNullOrEmpty()) "None"
                else "```fix\n${parent.aliases.joinToString("\n")}```"
                inline = true
            }
            field {
                this.name = "Uses"
                value = parent.uses.toString()
            }
            field {
                this.name = "Category"
                value = parent.category
                inline = true
            }
            field {
                this.name = "Channel"
                value = if (parent.channel() == "") "Global Macro"
                else event.guild.getChannel(Snowflake(parent.channel!!)).mention
                inline = true
            }
        }
    }

    fun addMacro(guild: GuildId, nameRaw: String, categoryRaw: String, channel: GuildMessageChannel?, contents: String, tracked: Boolean = false): String {
        val channelId = channel?.id?.asString ?: ""
        val name = nameRaw.toLowerCase()
        val category = categoryRaw.toLowerCase()

        if (name in allCommands) return "A command with that name already exists."

        val alias = store.findAlias(guild, name, category) { it }
        if (alias != null) return "A macro or alias with that name already exists."

        val result = store.forGuild(guild) {
            it.putIfAbsent("$name#$channelId", newMacro(name, contents, channelId, category, tracked))
        }

        return if (result == null) {
            "Success. Macro `$name` is now available ${if (channel == null) "globally" else "in channel ${channel.mention}"} and will respond with ```\n$contents\n```"
        } else {
            "A macro or alias with that name already exists."
        }
    }

    fun removeMacro(guild: GuildId, name_raw: String, channel: GuildMessageChannel?): String {
        val channelId = channel?.id?.value ?: ""
        val name = name_raw.toLowerCase()

        val result = store.forGuild(guild) {
            it.remove("$name#$channelId")
        }

        return if (result != null) {
            "Success. Macro `${result.displayNames()}` has been removed"
        } else {
            "Cannot find a macro by that name. If it is a channel specific macro you need to provide the channel as well."
        }
    }

    fun editMacro(guild: GuildId, name_raw: String, channel: GuildMessageChannel?, contents: String): String {
        val channelId = channel?.id?.value ?: ""
        val name = name_raw.toLowerCase()

        val macro = store.forGuild(guild) { macros ->
            macros["$name#$channelId"]?.let {
                it.contents = contents
                return@forGuild it
            }
            return@forGuild null
        }

        return if (macro != null) {
            val names = macro.displayNames()
            "Success. Macro `$names` available ${if (channel == null) "globally" else "in channel ${channel.mention}"} will now respond with ```\n$contents\n```"
        } else {
            "Cannot find a macro by that name. If it is a channel specific macro you need to provide the channel as well."
        }
    }

    suspend fun toggleTrackingForExistingMacro(guild: Guild, name_raw: String, channel: GuildMessageChannel?, tracking: Boolean): String {
        val channelId = channel?.id?.value ?: ""
        val name = name_raw.toLowerCase()

        val macro = store.forGuild(guild.id.value) { macros ->
            macros["$name#$channelId"]?.let {
                it.tracked = tracking
                return@forGuild it
            }
            return@forGuild null
        }

        return if (macro != null && tracking) {
            val names = macro.displayNames()
            "Success. Macro `$names` will now post an alert to ${guild.getChannel(Snowflake(configuration[guild.id.value]!!.alertChannel)).mention}"
        } else if (macro != null && !tracking) {
            val names = macro.displayNames()
            "Success. Macro `$names` will no longer post an alert to ${guild.getChannel(Snowflake(configuration[guild.id.value]!!.alertChannel)).mention}"
        } else {
            "Cannot find a macro by that name. If it is a channel specific macro you need to provide the channel as well."
        }
    }

    fun editMacroCategory(guild: GuildId, name_raw: String, channel: GuildMessageChannel?, category_raw: String): String {
        val channelId = channel?.id?.value ?: ""
        val name = name_raw.toLowerCase()
        val category = category_raw.toLowerCase()

        val macro = store.forGuild(guild) { macros ->
            macros["$name#$channelId"]?.let {
                it.category = category
                return@forGuild it
            }
            return@forGuild null
        }
        store.save()

        return if (macro != null) {
            val names = macro.displayNames()
            "Success. Macro `$names` available ${if (channel == null) "globally" else "in channel ${channel.mention}"} is now in category `${category}`"
        } else {
            "Cannot find a macro by that name. If it is a channel specific macro you need to provide the channel as well."
        }
    }

    fun addMacroAlias(guild: GuildId, name_raw: String, channel: GuildMessageChannel?, alias_raw: String): String {
        val channelId = channel?.id?.asString ?: ""
        val name = name_raw.toLowerCase()
        val alias = alias_raw.toLowerCase()

        if (alias in allCommands) return "A command with that alias already exists."

        val exists = store.findAlias(guild, alias, channelId) { it } != null
        if (exists) return "A macro or alias already exists by that name."

        val macro = store.forGuild(guild) { macros ->
            macros["$name#$channelId"]?.let {
                it.aliases.add(alias)
                return@forGuild it
            }
            return@forGuild null
        }

        return if (macro != null) {
            "Success. Macro `$name` now has the alias `$alias` ${if (channel == null) "globally" else "in channel ${channel.mention}"}"
        } else {
            "Cannot find a macro by that name. If it is a channel specific macro you need to provide the channel as well."
        }
    }

    fun removeMacroAlias(guild: GuildId, name_raw: String, channel: GuildMessageChannel?, alias_raw: String): String {
        val channelId = channel?.id?.value ?: ""
        val name = name_raw.toLowerCase()
        val alias = alias_raw.toLowerCase()


        // true -> removed macro successfully
        // false -> the alias was not found
        // null -> the macro was not found
        val result = store.forGuild(guild) { macros ->
            macros["$name#$channelId"]?.let {
                return@forGuild it.aliases.remove(alias)
            }
            return@forGuild null
        }

        return when (result) {
            true -> "Success. Macro `$name` no longer has the alias `$alias` ${if (channel == null) "globally" else "in channel ${channel.mention}"}"
            false -> "Cannot find the alias `$alias` of the macro. If it is a channel specific macro you need to provide the channel as well"
            null -> "Cannot find a macro by that name. If it is a channel specific macro you need to provide the channel as well."
        }
    }

    suspend fun listMacros(event: CommandEvent<*>, guild: GuildId, channel: GuildMessageChannel) = with(event) {
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
                                value = "```fix\n${sorted.joinToString("\n") { it.displayNames() }}\n```"
                                inline = true
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun listAllMacros(event: CommandEvent<*>, guild: Guild) {
        val allMacros = store.forGuild(guild.id.value) { it }
                .map { it.value }
                .groupBy {
                    it.channel?.toSnowflakeOrNull()?.let { guild.getChannel(it).name } ?: "Global Macros"
                }
                .toList()
                .sortedByDescending { it.second.size }

        val chunks = allMacros.chunked(1)
        event.respondMenu {
            chunks.map {
                page {
                    title = "All available macros"
                    color = event.discord.configuration.theme

                    if (it.isNotEmpty()) {
                        it.map { (channel, macros) ->
                            macros.chunked(15).map {
                                field {
                                    name = "**$channel**"
                                    value = "```fix\n${it.joinToString("\n") { it.displayNames() }}\n```"
                                    inline = true
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    suspend fun macroStats(event: CommandEvent<*>, guild: Guild, asc: Boolean) {
        val allMacros = store.forGuild(guild.id.value) { it }
                .map { it.value }
                .groupBy {
                    it.channel?.toSnowflakeOrNull()?.let { guild.getChannel(it).name } ?: "Global Macros"
                }
                .toList()
                .sortedByDescending { it.second.size }
                .map { (cat, macros) ->
                    cat to
                            if (asc) macros.sortedBy { it.uses }.take(10)
                            else macros.sortedByDescending { it.uses }.take(10)
                }

        val chunks = allMacros.chunked(1)
        event.respondMenu {
            chunks.map {
                page {
                    title = if (asc) "Least used macros" else "Top Used Macros"
                    color = event.discord.configuration.theme
                    if (it.isNotEmpty()) {
                        it.map { (channel, macros) ->
                            field {
                                name = "**$channel**"
                                value = "```properties\n" +
                                        macros.mapIndexed { i, m -> "${i + 1}. ${m.name} - ${m.uses} uses" }
                                                .joinToString("\n") +
                                        "```"
                                inline = true
                            }
                        }
                    }
                }
            }
        }

    }

    suspend fun searchMacro(event: CommandEvent<*>, query: String, channel: GuildMessageChannel, guild: GuildId) {
        val macros = getMacrosAvailableIn(guild, channel)
        val aliases = macros.flatMap { m -> m.aliases.toMutableList().also { it.add(m.name) } }
        val topNames = FuzzySearch.extractSorted(query, aliases, 70).take(5)
        val topContents = macros.map { it.name to FuzzySearch.tokenSetRatio(query, it.contents) }
            .filter { (_, score) -> score >= 70 }
            .sortedByDescending { it.second }
            .take(5)

        if (topNames.isEmpty() && topContents.isEmpty()) {
            event.respond("No results found")
            return
        }

        event.respond {
            title = "Search Results - '$query'"
            color = event.discord.configuration.theme

            field {
                name = "Top Results - By names and aliases"
                value = "```properties\n" + topNames.mapIndexed { i, result ->
                    "${i + 1}. ${result.string}"
                }.joinToString("\n") + "```"

                if (topNames.isEmpty()) value = "No results found"
            }
            field {
                name = "Top Results - By contents"
                value = "```properties\n" + topContents.mapIndexed { i, (name, score) ->
                    "${i + 1}. $name"
                }.joinToString("\n") + "```"
                if (topContents.isEmpty()) value = "No results found"
                inline = true
            }
        }
    }

    private fun getMacrosAvailableIn(guild: GuildId, channel: GuildMessageChannel): List<Macro> {
        val macroList = store.forGuild(guild) { macros ->
            macros.filterValues { it.canRun(channel) }
        }

        return macroList.filterKeys { key ->
            if (key.endsWith('#')) macroList.keys.none { it == "$key#${channel.id.value}" }
            else true
        }.map { it.value }
    }

    fun findMacro(guild: GuildId, name_raw: String, channel: MessageChannelBehavior): Macro? {
        val name = name_raw.toLowerCase()
        val channelId = channel.id.asString
        val macro = store.findAlias(guild, name, channelId) { it }
                ?: store.findAlias(guild, name, "") { it }

        if (macro != null) macro.uses++

        return macro
    }
}

val macroCooldown = mutableListOf<Pair<Snowflake, Macro>>()

fun macroListener(macroService: MacroService, configuration: Configuration) = listeners {
    on<MessageCreateEvent> {
        val guild = getGuild() ?: return@on
        val guildId = guild.id.value
        val member = member ?: return@on
        if (member.isIgnored(configuration)) {
            return@on
        }

        val guildConfiguration = configuration[guildId] ?: return@on
        val prefix = guildConfiguration.prefix

        if (!message.content.startsWith(prefix)) {
            return@on
        }

        val macroName = message.content
                .replace(prefix, "")
                .split("\\s".toRegex(), limit = 2)
                .firstOrNull()
                ?: return@on

        val macro = macroService.findMacro(guildId, macroName, message.channel) ?: return@on

        if (macroCooldown.contains(message.channelId to macro)) {
            message.addReaction(Emojis.clock4.toReaction())
            return@on
        }

        macroCooldown += message.channelId to macro
        launch {
            val cooldown = guildConfiguration.channelCooldown * 1000
            delay(cooldown.toLong())
            macroCooldown -= message.channelId to macro
        }

        if (message.content.startsWith("$prefix$prefix")) {
            message.addReaction(Emojis.eyes.toReaction())
        } else {
            message.delete()
        }


        val macroMessage = message.channel.createMessage(macro.contents)

        val logChannelId = configuration[guildId]?.logChannel ?: return@on

        guild.getChannelOf<GuildMessageChannel>(Snowflake(logChannelId))
                .createMessage("${member.username} :: ${member.id.value} " +
                        "invoked $macroName in ${message.channel.mention}")

        if (guildConfiguration.trackedMacrosEnabled && macro.tracked) {
            val alertChannelId = configuration[guildId]?.alertChannel ?: return@on

            guild.getChannelOf<GuildMessageChannel>(Snowflake(alertChannelId))
                .createEmbed {
                    title = "Tracked Macro Invoked"
                    color = discord.configuration.theme
                    description = """
                        **${macro.name}** invoked in ${message.channel.mention}. 
                        Staff action may be needed [here](${macroMessage.jumpLink()}).
                    """.trimIndent()
                    footer {
                        icon = member.pfpUrl
                        text = "Invoked by ${member.tag} :: ${member.id.value}"
                    }
                }
        }
    }
}