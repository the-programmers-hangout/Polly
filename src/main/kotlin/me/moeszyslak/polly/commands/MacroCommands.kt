package me.moeszyslak.polly.commands

import com.gitlab.kordlib.core.entity.channel.TextChannel
import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.ChannelArg
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.moeszyslak.polly.extensions.requiredPermissionLevel
import me.moeszyslak.polly.services.MacroService
import me.moeszyslak.polly.services.Permission

fun macroCommands(macroService: MacroService) = commands("Macros") {
    guildCommand("add") {
        description = "Adds a macro (for all channels)"
        requiredPermissionLevel = Permission.STAFF

        execute(AnyArg("Name"),
                AnyArg("Category"),
                EveryArg("Contents")) {
            val (name, category, contents) = args

            respond(macroService.addMacro(guild.id.longValue, name, category, null, contents))
        }
    }

    guildCommand("add-channel") {
        description = "Adds a macro to a specific channel"
        requiredPermissionLevel = Permission.STAFF

        execute(AnyArg("Name"),
                AnyArg("Category"),
                ChannelArg<TextChannel>("Channel"),
                EveryArg("Contents")) {
            val (name, category, channel, contents) = args

            respond(macroService.addMacro(guild.id.longValue, name, category, channel, contents))
        }
    }

    guildCommand("remove") {
        description = "Removes a macro"
        requiredPermissionLevel = Permission.STAFF
        execute(AnyArg("Name"), ChannelArg<TextChannel>("Channel").makeNullableOptional()) {
            respond(macroService.removeMacro(guild.id.longValue, args.first, args.second))
        }
    }

    guildCommand("edit") {
        description = "Edits the contents of a macro"
        requiredPermissionLevel = Permission.STAFF
        execute(AnyArg("Name"), ChannelArg<TextChannel>("Channel").makeNullableOptional(), EveryArg("Contents")) {
            respond(macroService.editMacro(guild.id.longValue, args.first, args.second, args.third))
        }
    }

    guildCommand("editcategory") {
        description = "Edits the category of a macro"
        requiredPermissionLevel = Permission.STAFF
        execute(AnyArg("Name"), ChannelArg<TextChannel>("Channel").makeNullableOptional(), AnyArg("New Category")) {
            respond(macroService.editMacroCategory(guild.id.longValue, args.first, args.second, args.third))
        }
    }

    guildCommand("list") {
        description = "Lists all macros available in the given channel. If no channel is specified, defaults to the current channel."
        requiredPermissionLevel = Permission.USER
        execute(ChannelArg<TextChannel>("Channel").makeOptional { it.channel as TextChannel }) {
            macroService.listMacros(this, guild.id.longValue, args.first)
        }
    }

    guildCommand("listall") {
        description = "Lists all macros available in the guild.id.longValue, grouped by channel."
        requiredPermissionLevel = Permission.USER
        execute {
            macroService.listAllMacros(this, guild)
        }
    }
}
