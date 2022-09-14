package me.moeszyslak.polly.commands

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.interaction.GuildAutoCompleteInteraction
import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.commands.subcommand
import me.moeszyslak.polly.services.MacroService

fun macroSubcommands(macroService: MacroService) = subcommand("Macros") {
    sub("Add", "Adds a macro (for all channels)") {
        execute(
            AnyArg("Name"),
            AnyArg("Category"),
            EveryArg("Contents")
        ) {
            val (name, category, contents) = args
            respond(macroService.addMacro(guild.id, name, category, null, contents))
        }
    }

    sub("AddChannelMacro", "Adds a macro to a specific channel") {
        execute(
            AnyArg("Name"),
            AnyArg("Category"),
            ChannelArg<GuildMessageChannel>("Channel"),
            EveryArg("Contents")
        ) {
            val (name, category, channel, contents) = args
            respond(macroService.addMacro(guild.id, name, category, channel, contents))
        }
    }

    sub("AddTrackedMacro", "Adds a tracked macro (for all channels)") {
        execute(
            AnyArg("Name"),
            AnyArg("Category"),
            EveryArg("Contents")
        ) {
            val (name, category, contents) = args
            respond(macroService.addMacro(guild.id, name, category, null, contents, true))
        }
    }

    sub("Track", "Converts an existing macro to a tracked (alert) macro") {
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.toggleTrackingForExistingMacro(guild, args.first, args.second, true))
        }
    }

    sub("Untrack", "Removes tracking from an existing macro") {
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.toggleTrackingForExistingMacro(guild, args.first, args.second, false))
        }
    }

    sub("Remove", "Removes a macro") {
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.removeMacro(guild.id, args.first, args.second))
        }
    }

    sub("Edit", "Edits the contents of a macro") {
        execute(AnyArg("Name"), EveryArg("Contents"),  ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.editMacro(guild.id, args.first, args.third, args.second))
        }
    }

    sub("EditCategory", "Edits the category of a macro") {
        execute(AnyArg("Name"),  AnyArg("Category"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.editMacroCategory(guild.id, args.first, args.third, args.second))
        }
    }

    sub("AddAlias", "Add an alias to a macro") {
        execute(AnyArg("Name"), AnyArg("Alias"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.addMacroAlias(guild.id, args.first, args.third, args.second))
        }
    }

    sub("RemoveAlias", "Remove an alias from a macro") {
        execute(AnyArg("Name"), AnyArg("Alias"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.removeMacroAlias(guild.id, args.first, args.third, args.second))
        }
    }
}

fun macroCommands(macroService: MacroService) = commands("Macros") {
    slash("MacroInfo", "Get Information for a macro", Permissions(Permission.UseApplicationCommands)) {
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            macroService.macroInfo(this, guild.id, args.first, args.second)
        }
    }


    slash(
        "ListMacros",
        "Lists all macros available in the given channel.",
        Permissions(Permission.UseApplicationCommands)
    ) {
        execute(ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            var channelName = args.first
            if (channelName == null) {
                channelName = channel as GuildMessageChannel
            }
            val interactionResponse = interaction?.deferPublicResponse() ?: return@execute
            macroService.listMacros(this, guild.id, channelName)
            interactionResponse.respond { content = "Available Macros:" }
        }
    }

    slash(
        "ListAllMacros",
        "Lists all macros available in the guild, grouped by channel.",
        Permissions(Permission.UseApplicationCommands)
    ) {
        execute {
            val interactionResponse = interaction?.deferPublicResponse() ?: return@execute
            macroService.listAllMacros(this, guild)
            interactionResponse.respond { content = "Available Macros:" }
        }
    }

    slash(
        "MacroStats",
        "Get statistics on most and least used macros",
        Permissions(Permission.UseApplicationCommands)
    ) {
        execute(ChoiceArg("option", "asc", "desc").optional("desc")) {
            when (args.first.lowercase()) {
                "asc" -> macroService.macroStats(this, guild, true)
                "desc" -> macroService.macroStats(this, guild, false)
            }
        }
    }

    slash("SearchMacros", "Search the available macros available", Permissions(Permission.UseApplicationCommands)) {
        execute(EveryArg) {
            val (query) = args
            macroService.searchMacro(this, query, channel as GuildMessageChannel, guild.id)
        }
    }

    fun autocompleteMacroArg() = AnyArg("Macro", "Macro name to send").autocomplete {
        val guild = (interaction as GuildAutoCompleteInteraction).getGuild()
        val channel = (interaction as GuildAutoCompleteInteraction).channel.asChannel()
        macroService.getMacrosAvailableIn(guild.id, channel).filter { it.name.contains(input) }.map { it.name }
    }

    slash("macro", "Search and send a macro", Permissions(Permission.UseApplicationCommands)) {
        execute(
            autocompleteMacroArg(),
            MemberArg("Target", "Optional user to tag in macro response").optionalNullable(null)
        ) {
            val (name, target) = args
            val macro = macroService.findMacro(guild.id, name, channel) ?: return@execute
            if (target != null) {
                respondPublic("${target.mention} ${macro.contents}")
            } else respondPublic(macro.contents)
        }
    }
}
