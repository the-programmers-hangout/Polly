package me.moeszyslak.polly.services

import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.moeszyslak.polly.data.Configuration
import me.moeszyslak.polly.utilities.timeToString
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Service
class StatisticsService(private val configuration: Configuration, private val discord: Discord) {
    private var startTime: Date = Date()

    val uptime: String
        get() = timeToString(Date().time - startTime.time)


    val ping: String
        get() = "${discord.kord.gateway.averagePing}"

}