group = "me.moeszyslak"
version = Versions.BOT
description = "A simple, elegant macro bot"

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
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
        dependsOn("writeProperties")
    }

    register<WriteProperties>("writeProperties") {
        property("name", project.name)
        property("description", project.description.toString())
        property("version", version.toString())
        property("url", "https://github.com/the-programmers-hangout/Polly")
        setOutputFile("src/main/resources/bot.properties")
    }

    shadowJar {
        archiveFileName.set("Polly.jar")
        manifest {
            attributes("Main-Class" to "me.moeszyslak.polly.MainAppKt")
        }
    }
}

object Versions {
    const val BOT = "2.0.0-RC1"
    const val DISCORDKT = "0.23.4"
    const val FUZZY = "1.3.1"
}