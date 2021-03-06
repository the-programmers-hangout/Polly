package me.moeszyslak.polly.preconditions

import com.gitlab.kordlib.core.entity.Member
import me.jakejmattson.discordkt.api.dsl.*
import me.moeszyslak.polly.commands.isIgnored
import me.moeszyslak.polly.data.Configuration
import me.moeszyslak.polly.extensions.requiredPermissionLevel
import me.moeszyslak.polly.services.PermissionsService


fun ignoreListPrecondition(configuration: Configuration) = precondition {
    val guild = guild ?: return@precondition fail()
    val member = author.asMember(guild.id)
    if (member.isIgnored(configuration)) {
        fail()
    }
}
