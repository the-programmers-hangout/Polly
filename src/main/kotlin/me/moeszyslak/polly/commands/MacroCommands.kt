package me.moeszyslak.polly.commands

import dev.kord.core.entity.channel.TextChannel
import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.ChannelArg
import me.jakejmattson.discordkt.api.arguments.ChoiceArg
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.commands.commands
import me.moeszyslak.polly.data.Permissions
import me.moeszyslak.polly.services.MacroService

fun macroCommands(macroService: MacroService) = commands("Macros") {
    guildCommand("MacroInfo") {
        description = "Get Information for a macro"
        requiredPermission = Permissions.NONE

        execute(AnyArg("Name"), ChannelArg<TextChannel>("Channel").optionalNullable()) {
            macroService.macroInfo(this, guild.id.value, args.first, args.second)
        }
    }

    guildCommand("AddMacro") {
        description = "Adds a macro (for all channels)"
        requiredPermission = Permissions.STAFF

        execute(AnyArg("Name"),
                AnyArg("Category"),
                EveryArg("Contents")) {
            val (name, category, contents) = args

            respond(macroService.addMacro(guild.id.value, name, category, null, contents))
        }
    }

    guildCommand("AddChannelMacro") {
        description = "Adds a macro to a specific channel"
        requiredPermission = Permissions.STAFF

        execute(AnyArg("Name"),
                AnyArg("Category"),
                ChannelArg<TextChannel>("Channel"),
                EveryArg("Contents")) {
            val (name, category, channel, contents) = args

            respond(macroService.addMacro(guild.id.value, name, category, channel, contents))
        }
    }

    guildCommand("RemoveMacro") {
        description = "Removes a macro"
        requiredPermission = Permissions.STAFF
        execute(AnyArg("Name"), ChannelArg<TextChannel>("Channel").optionalNullable()) {
            respond(macroService.removeMacro(guild.id.value, args.first, args.second))
        }
    }

    guildCommand("EditMacro") {
        description = "Edits the contents of a macro"
        requiredPermission = Permissions.STAFF
        execute(AnyArg("Name"), ChannelArg<TextChannel>("Channel").optionalNullable(), EveryArg("Contents")) {
            respond(macroService.editMacro(guild.id.value, args.first, args.second, args.third))
        }
    }

    guildCommand("EditCategory") {
        description = "Edits the category of a macro"
        requiredPermission = Permissions.STAFF
        execute(AnyArg("Name"), ChannelArg<TextChannel>("Channel").optionalNullable(), AnyArg("New Category")) {
            respond(macroService.editMacroCategory(guild.id.value, args.first, args.second, args.third))
        }
    }

    guildCommand("AddAlias") {
        description = "Add an alias to a macro"
        requiredPermission = Permissions.STAFF
        execute(AnyArg("Name"), ChannelArg<TextChannel>("Channel").optionalNullable(), AnyArg("Alias")) {
            respond(macroService.addMacroAlias(guild.id.value, args.first, args.second, args.third))
        }
    }

    guildCommand("RemoveAlias") {
        description = "Remove an alias from a macro"
        requiredPermission = Permissions.STAFF
        execute(AnyArg("Name"), ChannelArg<TextChannel>("Channel").optionalNullable(), AnyArg("Alias")) {
            respond(macroService.removeMacroAlias(guild.id.value, args.first, args.second, args.third))
        }
    }

    guildCommand("ListMacros") {
        description = "Lists all macros available in the given channel. If no channel is specified, defaults to the current channel."
        requiredPermission = Permissions.NONE
        execute(ChannelArg<TextChannel>("Channel").optional() { it.channel as TextChannel }) {
            macroService.listMacros(this, guild.id.value, args.first)
        }
    }

    guildCommand("ListAllMacros") {
        description = "Lists all macros available in the guild, grouped by channel."
        requiredPermission = Permissions.NONE
        execute {
            macroService.listAllMacros(this, guild)
        }
    }

    guildCommand("MacroStats") {
        description = "Get statistics on most and least used macros"
        requiredPermission = Permissions.NONE
        execute(ChoiceArg("asc/desc", "asc", "desc").optional("desc")) {
            when (args.first.toLowerCase()) {
                "asc" -> macroService.macroStats(this, guild, true)
                "desc" -> macroService.macroStats(this, guild, false)
            }
        }
    }

    guildCommand("SearchMacros") {
       description = "Search the available macros available"
        requiredPermission = Permissions.NONE
        execute(EveryArg) {
            val (query) = args
            macroService.searchMacro(this, query, channel, guild.id.value)
        }
    }
}
