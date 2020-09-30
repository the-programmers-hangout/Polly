package me.moeszyslak.macaroni.commands

import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.services.ConversationService
import me.moeszyslak.macaroni.conversations.ConfigurationConversation
import me.moeszyslak.macaroni.data.Configuration
import me.moeszyslak.macaroni.extensions.requiredPermissionLevel
import me.moeszyslak.macaroni.services.Permission

fun guildConfigurationCommands(configuration: Configuration, conversationService: ConversationService) = commands("Basics") {

    command("Setup") {
        description = "Setup a guild to use Macaroni"
        requiredPermissionLevel = Permission.GUILD_OWNER
        execute {
            if (configuration.hasGuildConfig(guild!!.id.longValue))
                return@execute respond("Guild configuration already exists. You can use commands to modify the config")

            conversationService.startPublicConversation<ConfigurationConversation>(author, channel.asChannel(), guild!!)
            respond("${guild!!.name} has been setup")
        }
    }
}