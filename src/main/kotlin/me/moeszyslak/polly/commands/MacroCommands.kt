package me.moeszyslak.polly.commands

import dev.kord.core.entity.channel.GuildMessageChannel
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.ChannelArg
import me.jakejmattson.discordkt.arguments.ChoiceArg
import me.jakejmattson.discordkt.arguments.EveryArg
import me.jakejmattson.discordkt.commands.commands
import me.moeszyslak.polly.data.Permissions
import me.moeszyslak.polly.services.MacroService

fun macroCommands(macroService: MacroService) = commands("Macros") {
    command("MacroInfo") {
        description = "Get Information for a macro"
        requiredPermission = Permissions.NONE

        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            macroService.macroInfo(this, guild.id.value, args.first, args.second)
        }
    }

    command("AddMacro") {
        description = "Adds a macro (for all channels)"
        requiredPermission = Permissions.STAFF

        execute(AnyArg("Name"),
                AnyArg("Category"),
                EveryArg("Contents")) {
            val (name, category, contents) = args

            respond(macroService.addMacro(guild.id.value, name, category, null, contents))
        }
    }

    command("AddChannelMacro") {
        description = "Adds a macro to a specific channel"
        requiredPermission = Permissions.STAFF

        execute(AnyArg("Name"),
                AnyArg("Category"),
                ChannelArg<GuildMessageChannel>("Channel"),
                EveryArg("Contents")) {
            val (name, category, channel, contents) = args

            respond(macroService.addMacro(guild.id.value, name, category, channel, contents))
        }
    }

    command("RemoveMacro") {
        description = "Removes a macro"
        requiredPermission = Permissions.STAFF
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable()) {
            respond(macroService.removeMacro(guild.id.value, args.first, args.second))
        }
    }

    command("EditMacro") {
        description = "Edits the contents of a macro"
        requiredPermission = Permissions.STAFF
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable(), EveryArg("Contents")) {
            respond(macroService.editMacro(guild.id.value, args.first, args.second, args.third))
        }
    }

    command("EditCategory") {
        description = "Edits the category of a macro"
        requiredPermission = Permissions.STAFF
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable(), AnyArg("New Category")) {
            respond(macroService.editMacroCategory(guild.id.value, args.first, args.second, args.third))
        }
    }

    command("AddAlias") {
        description = "Add an alias to a macro"
        requiredPermission = Permissions.STAFF
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable(), AnyArg("Alias")) {
            respond(macroService.addMacroAlias(guild.id.value, args.first, args.second, args.third))
        }
    }

    command("RemoveAlias") {
        description = "Remove an alias from a macro"
        requiredPermission = Permissions.STAFF
        execute(AnyArg("Name"), ChannelArg<GuildMessageChannel>("Channel").optionalNullable(), AnyArg("Alias")) {
            respond(macroService.removeMacroAlias(guild.id.value, args.first, args.second, args.third))
        }
    }

    command("ListMacros") {
        description = "Lists all macros available in the given channel. If no channel is specified, defaults to the current channel."
        requiredPermission = Permissions.NONE
        execute(ChannelArg<GuildMessageChannel>("Channel").optional() { it.channel as GuildMessageChannel }) {
            macroService.listMacros(this, guild.id.value, args.first)
        }
    }

    command("ListAllMacros") {
        description = "Lists all macros available in the guild, grouped by channel."
        requiredPermission = Permissions.NONE
        execute {
            macroService.listAllMacros(this, guild)
        }
    }

    command("MacroStats") {
        description = "Get statistics on most and least used macros"
        requiredPermission = Permissions.NONE
        execute(ChoiceArg("asc/desc", "asc", "desc").optional("desc")) {
            when (args.first.toLowerCase()) {
                "asc" -> macroService.macroStats(this, guild, true)
                "desc" -> macroService.macroStats(this, guild, false)
            }
        }
    }

    command("SearchMacros") {
       description = "Search the available macros available"
        requiredPermission = Permissions.NONE
        execute(EveryArg) {
            val (query) = args
            macroService.searchMacro(this, query, channel, guild.id.value)
        }
    }
}
