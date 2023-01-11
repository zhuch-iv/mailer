package net.zhu4.mailer.adapter.discord

import discord4j.core.event.domain.Event
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

interface DiscordEventListener<T : Event> {

    fun getEventType(): Class<T>

    fun execute(event: T): Mono<Void>

    fun handleError(throwable: Throwable): Mono<Void> {
        log.error("Unable to process ${getEventType()}", throwable)
        return Mono.empty()
    }

    companion object {
        private val log = LoggerFactory.getLogger(DiscordEventListener::class.java)
    }
}
