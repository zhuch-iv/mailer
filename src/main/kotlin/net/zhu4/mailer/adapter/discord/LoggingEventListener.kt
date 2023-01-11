package net.zhu4.mailer.adapter.discord

import discord4j.core.event.domain.Event
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

//@Component
class LoggingEventListener : DiscordEventListener<Event> {

    override fun getEventType(): Class<Event> {
        return Event::class.java
    }

    override fun execute(event: Event): Mono<Void> {
        return Mono.just(event)
                .doOnNext { log.info("$it") }
                .then()
    }

    companion object {
        private val log = LoggerFactory.getLogger(LoggingEventListener::class.java)
    }
}
