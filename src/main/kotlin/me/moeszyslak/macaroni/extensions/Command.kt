package me.moeszyslak.macaroni.extensions

import me.jakejmattson.discordkt.api.dsl.Command
import me.moeszyslak.macaroni.services.DEFAULT_REQUIRED_PERMISSION
import me.moeszyslak.macaroni.services.Permission
import java.util.*


private object CommandPropertyStore {
    val permissions = WeakHashMap<Command, Permission>()
}

var Command.requiredPermissionLevel: Permission
    get() = CommandPropertyStore.permissions[this] ?: DEFAULT_REQUIRED_PERMISSION
    set(value) {
        CommandPropertyStore.permissions[this] = value
    }