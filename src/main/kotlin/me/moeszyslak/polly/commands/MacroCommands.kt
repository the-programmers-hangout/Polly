package me.moeszyslak.polly.commands

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.interaction.GuildAutoCompleteInteraction
import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.commands.subcommand
import me.moeszyslak.polly.services.MacroService

fun autocompleteMacroArg(macroService: MacroService) = AnyArg("Macro", "The name of an existing macro").autocomplete {
    val guild = (interaction as GuildAutoCompleteInteraction).getGuild()
    val channel = (interaction as GuildAutoCompleteInteraction).channel.asChannel()
    macroService.getMacrosAvailableIn(guild.id, channel).filter { it.name.contains(input) }.map { it.name }
}

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
        execute(autocompleteMacroArg(macroService), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.toggleTrackingForExistingMacro(guild, args.first, args.second, true))
        }
    }

    sub("Untrack", "Removes tracking from an existing macro") {
        execute(autocompleteMacroArg(macroService), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.toggleTrackingForExistingMacro(guild, args.first, args.second, false))
        }
    }

    sub("Remove", "Removes a macro") {
        execute(autocompleteMacroArg(macroService), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.removeMacro(guild.id, args.first, args.second))
        }
    }

    sub("Edit", "Edits the contents of a macro") {
        execute(autocompleteMacroArg(macroService), EveryArg("Contents"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.editMacro(guild.id, args.first, args.third, args.second))
        }
    }

    sub("EditCategory", "Edits the category of a macro") {
        execute(autocompleteMacroArg(macroService), AnyArg("Category"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.editMacroCategory(guild.id, args.first, args.third, args.second))
        }
    }

    sub("AddAlias", "Add an alias to a macro") {
        execute(autocompleteMacroArg(macroService), AnyArg("Alias"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.addMacroAlias(guild.id, args.first, args.third, args.second))
        }
    }

    sub("RemoveAlias", "Remove an alias from a macro") {
        execute(autocompleteMacroArg(macroService), AnyArg("Alias"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.removeMacroAlias(guild.id, args.first, args.third, args.second))
        }
    }
}

fun macroCommands(macroService: MacroService) = commands("Macros", Permissions(Permission.UseApplicationCommands)) {
    slash("MacroInfo", "Get Information for a macro") {
        execute(autocompleteMacroArg(macroService), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            macroService.macroInfo(this, guild.id, args.first, args.second)
        }
    }

    slash("ListMacros", "Lists all macros available in the given channel.") {
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

    slash("ListAllMacros", "Lists all macros available in the guild, grouped by channel.") {
        execute {
            val interactionResponse = interaction?.deferPublicResponse() ?: return@execute
            macroService.listAllMacros(this, guild)
            interactionResponse.respond { content = "Available Macros:" }
        }
    }

    slash("MacroStats", "Get statistics on most and least used macros") {
        execute(ChoiceArg("option", "asc", "desc").optional("desc")) {
            when (args.first.lowercase()) {
                "asc" -> macroService.macroStats(this, guild, true)
                "desc" -> macroService.macroStats(this, guild, false)
            }
        }
    }

    slash("SearchMacros", "Search the available macros available") {
        execute(EveryArg) {
            val (query) = args
            macroService.searchMacro(this, query, channel as GuildMessageChannel, guild.id)
        }
    }

    slash("macro", "Search and send a macro") {
        execute(
            autocompleteMacroArg(macroService),
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
