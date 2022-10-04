package me.moeszyslak.polly

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.gateway.PrivilegedIntent
import dev.kord.x.emoji.Emojis
import me.jakejmattson.discordkt.dsl.bot
import me.moeszyslak.polly.data.Configuration
import me.moeszyslak.polly.data.MacroStore
import java.awt.Color

@KordPreview
@PrivilegedIntent
fun main() {
    val token = System.getenv("BOT_TOKEN") ?: null
    val prefix = System.getenv("DEFAULT_PREFIX") ?: "<none>"
    require(token != null) { "Expected the bot token as an environment variable" }

    bot(token) {
        val configuration = data("config/config.json") { Configuration() }
        data("config/macros.json") { MacroStore() }

        prefix {
            guild?.let { configuration[it.id]?.prefix } ?: prefix
        }

        configure {
            mentionAsPrefix = true
            documentCommands = true
            logStartup = true
            dualRegistry = false
            commandReaction = Emojis.eyes
            theme = Color(0x00BFFF)
            recommendCommands = false
            defaultPermissions = Permissions(Permission.ManageMessages)
        }

        onStart {
            getInjectionObjects<MacroStore>().populate()
        }
    }
}