group = "me.moeszyslak"
version = Versions.BOT
description = "A simple, elegant macro bot"

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("me.jakejmattson:DiscordKt:${Versions.DISCORDKT}")
    implementation("me.xdrop:fuzzywuzzy:${Versions.FUZZY}")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    shadowJar {
        archiveFileName.set("Polly.jar")
        manifest {
            attributes("Main-Class" to "me.moeszyslak.polly.MainAppKt")
        }
    }
}

object Versions {
    const val BOT = "1.0.0"
    const val DISCORDKT = "0.23.4"
    const val FUZZY = "1.3.1"
}