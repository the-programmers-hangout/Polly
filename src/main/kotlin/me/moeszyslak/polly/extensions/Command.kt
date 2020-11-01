package me.moeszyslak.polly.extensions

import me.jakejmattson.discordkt.api.dsl.Command
import me.moeszyslak.polly.services.DEFAULT_REQUIRED_PERMISSION
import me.moeszyslak.polly.services.Permission
import java.util.*


private object CommandPropertyStore {
    val permissions = WeakHashMap<Command, Permission>()
}

var Command.requiredPermissionLevel: Permission
    get() = CommandPropertyStore.permissions[this] ?: DEFAULT_REQUIRED_PERMISSION
    set(value) {
        CommandPropertyStore.permissions[this] = value
    }