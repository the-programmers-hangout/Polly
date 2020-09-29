package me.moeszyslak.macaroni.commands

import com.gitlab.kordlib.kordx.emoji.Emojis
import me.jakejmattson.discordkt.api.dsl.commands
import java.awt.Color

fun basics() = commands("Basics") {

    command("ping") {
        description = "test"
        execute {
            respond("pong")
        }
    }
}