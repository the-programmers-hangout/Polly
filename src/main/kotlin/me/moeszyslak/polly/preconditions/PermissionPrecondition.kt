package me.moeszyslak.polly.preconditions

import me.jakejmattson.discordkt.api.dsl.*
import me.moeszyslak.polly.extensions.requiredPermissionLevel
import me.moeszyslak.polly.services.PermissionsService


fun permissionPrecondition(permissionsService: PermissionsService) = precondition {
    val command = command ?: return@precondition fail()
    val requiredPermissionLevel = command.requiredPermissionLevel
    val guild = guild ?: return@precondition fail()
    val member = author.asMember(guild.id)

    if (!permissionsService.hasClearance(member, requiredPermissionLevel))
        fail()
}