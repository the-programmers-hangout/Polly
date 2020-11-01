package me.moeszyslak.polly.commands

import com.gitlab.kordlib.core.entity.channel.TextChannel
import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.ChannelArg
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.moeszyslak.polly.extensions.requiredPermissionLevel
import me.moeszyslak.polly.services.Permission

fun macroCommands() = commands("Macros") {
    command("addmacro") {
        description = "Adds a macro to a specific channel or globally, if no channel is given"
        requiredPermissionLevel = Permission.STAFF
//        usageExamples = listOf(
//                "coolname Miscellaneous This is a global macro",
//                "promises Programming #javascript Channel specific macro"
//        )
        execute(AnyArg("Name"),
                AnyArg("Category"),
                ChannelArg<TextChannel>("Channel").makeNullableOptional(),
                EveryArg("Contents")) {
            val (name, category, channel, contents) = args

//            respond(macroService.addMacro(guild, name, category, channel, contents))
        }
    }
}