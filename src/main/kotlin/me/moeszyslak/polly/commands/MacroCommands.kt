package me.moeszyslak.polly.commands

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.entity.channel.GuildMessageChannel
import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.commands
import me.moeszyslak.polly.services.MacroService

fun macroCommands(macroService: MacroService) = commands("Macros") {
    command("AddMacro") {
        description = "Adds a macro (for all channels)"
        execute(AnyArg("Name"),
            AnyArg("Category"),
            EveryArg("Contents")) {
            val (name, category, contents) = args

            respond(macroService.addMacro(guild.id.value, name, category, null, contents))
        }
    }

    command("AddChannelMacro") {
        description = "Adds a macro to a specific channel"
        execute(AnyArg("Name"),
            AnyArg("Category"),
            ChannelArg<GuildMessageChannel>("Channel"),
            EveryArg("Contents")) {
            val (name, category, channel, contents) = args

            respond(macroService.addMacro(guild.id.value, name, category, channel, contents))
        }
    }

    command("AddTrackedMacro") {
        description = "Adds a tracked macro (for all channels)"
        execute(AnyArg("Name"),
            AnyArg("Category"),
            EveryArg("Contents")) {
            val (name, category, contents) = args

            respond(macroService.addMacro(guild.id.value, name, category, null, contents, true))
        }
    }

    command("Track") {
        description = "Converts an existing macro to a tracked (alert) macro"
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.toggleTrackingForExistingMacro(guild, args.first, args.second, true))
        }
    }

    command("Untrack") {
        description = "Removes tracking from an existing macro"
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.toggleTrackingForExistingMacro(guild, args.first, args.second, false))
        }
    }

    command("RemoveMacro") {
        description = "Removes a macro"
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.removeMacro(guild.id.value, args.first, args.second))
        }
    }

    command("EditMacro") {
        description = "Edits the contents of a macro"
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable(), EveryArg("Contents")) {
            respond(macroService.editMacro(guild.id.value, args.first, args.second, args.third))
        }
    }

    command("EditCategory") {
        description = "Edits the category of a macro"
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable(), AnyArg("New Category")) {
            respond(macroService.editMacroCategory(guild.id.value, args.first, args.second, args.third))
        }
    }

    command("AddAlias") {
        description = "Add an alias to a macro"
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable(), AnyArg("Alias")) {
            respond(macroService.addMacroAlias(guild.id.value, args.first, args.second, args.third))
        }
    }

    command("RemoveAlias") {
        description = "Remove an alias from a macro"
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable(), AnyArg("Alias")) {
            respond(macroService.removeMacroAlias(guild.id.value, args.first, args.second, args.third))
        }
    }

    command("MacroInfo") {
        description = "Get Information for a macro"
        requiredPermissions = Permissions(Permission.UseApplicationCommands)
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            macroService.macroInfo(this, guild.id.value, args.first, args.second)
        }
    }


    command("ListMacros") {
        description = "Lists all macros available in the given channel. If no channel is specified, defaults to the current channel."
        requiredPermissions = Permissions(Permission.UseApplicationCommands)
        execute(ChannelArg<GuildMessageChannel>("Channel").optional { it.channel as GuildMessageChannel }) {
            macroService.listMacros(this, guild.id.value, args.first)
        }
    }

    command("ListAllMacros") {
        description = "Lists all macros available in the guild, grouped by channel."
        requiredPermissions = Permissions(Permission.UseApplicationCommands)
        execute {
            macroService.listAllMacros(this, guild)
        }
    }

    command("MacroStats") {
        description = "Get statistics on most and least used macros"
        requiredPermissions = Permissions(Permission.UseApplicationCommands)
        execute(ChoiceArg("asc/desc", "asc", "desc").optional("desc")) {
            when (args.first.lowercase()) {
                "asc" -> macroService.macroStats(this, guild, true)
                "desc" -> macroService.macroStats(this, guild, false)
            }
        }
    }

    command("SearchMacros") {
        description = "Search the available macros available"
        requiredPermissions = Permissions(Permission.UseApplicationCommands)
        execute(EveryArg) {
            val (query) = args
            macroService.searchMacro(this, query, channel, guild.id.value)
        }
    }
}
