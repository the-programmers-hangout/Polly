package me.moeszyslak.polly.preconditions

import me.jakejmattson.discordkt.api.dsl.*
import me.moeszyslak.polly.extensions.requiredPermissionLevel
import me.moeszyslak.polly.services.PermissionsService


class PermissionPrecondition(private val permissionsService: PermissionsService) : Precondition() {
    override suspend fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val command = event.command ?: return Fail()
        val requiredPermissionLevel = command.requiredPermissionLevel
        val guild = event.guild!!
        val member = event.author.asMember(guild.id)


        if (!permissionsService.hasClearance(member, requiredPermissionLevel))
            return Fail()

        return Pass
    }
}