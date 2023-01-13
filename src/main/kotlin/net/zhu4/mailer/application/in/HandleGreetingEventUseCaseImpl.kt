package net.zhu4.mailer.application.`in`

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class HandleGreetingEventUseCaseImpl: HandleGreetingEventUseCase {
    override fun greeting(event: ChatInputInteractionEvent): Mono<Void> {
        log.info("Event: ${event.getOption("template")} ${event.getOption("file")} $event")
        return event.createFollowup("OK!")
            .then()
    }

    companion object {
        private val log = LoggerFactory.getLogger(HandleGreetingEventUseCaseImpl::class.java)
    }
}
