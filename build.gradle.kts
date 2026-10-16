group = "me.moeszyslak"
version = "2.0.0-RC1"
description = "A simple, elegant macro bot"

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.discordkt)
    implementation(libs.fuzzywuzzy)
}

kotlin {
    jvmToolchain(17)
}

tasks {
    shadowJar {
        archiveFileName = "Polly.jar"
        manifest {
            attributes("Main-Class" to "me.moeszyslak.polly.MainAppKt")
        }
    }
}

val writeProperties = tasks.register<WriteProperties>("writeProperties") {
    property("name", project.name)
    property("description", project.description.toString())
    property("version", version.toString())
    property("url", "https://github.com/the-programmers-hangout/Polly")
    destinationFile = layout.buildDirectory.file("bot-resources/bot.properties")
}

sourceSets {
    main {
        output.dir(writeProperties)
    }
}