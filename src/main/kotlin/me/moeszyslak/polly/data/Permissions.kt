package me.moeszyslak.polly.data

import dev.kord.common.entity.Snowflake
import me.jakejmattson.discordkt.dsl.Permission
import me.jakejmattson.discordkt.dsl.PermissionSet
import me.jakejmattson.discordkt.dsl.permission
import me.jakejmattson.discordkt.extensions.toSnowflake

@Suppress("unused")

object Permissions : PermissionSet {
    val BOT_OWNER = permission("Bot Owner") { users(discord.getInjectionObjects<Configuration>().botOwner.toSnowflake()) }
    val GUILD_OWNER = permission("Guild Owner") { guild?.ownerId?.let { users(it) } }
    val STAFF = permission("Staff") {
        discord.getInjectionObjects<Configuration>()[guild!!.id.value]?.staffRole?.let {
            roles(Snowflake(it))
        }
    }
    val NONE = permission("None") { guild?.everyoneRole?.let { roles(it.id) } }
    override val hierarchy: List<Permission> = listOf(NONE, STAFF, GUILD_OWNER, BOT_OWNER)
    override val commandDefault: Permission = STAFF
}