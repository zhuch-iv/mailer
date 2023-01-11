package net.zhu4.mailer.adapter.discord

import discord4j.core.event.domain.message.MessageCreateEvent
import net.zhu4.mailer.application.`in`.ProcessDiscordMessageUseCase
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MessageCreateListener(
    private val useCase: ProcessDiscordMessageUseCase
) : DiscordEventListener<MessageCreateEvent> {

    override fun getEventType(): Class<MessageCreateEvent> {
        return MessageCreateEvent::class.java
    }

    override fun execute(event: MessageCreateEvent): Mono<Void> {
        log.debug("Get event: $event")
        return useCase.processMessage(event.message)
    }

    companion object {
        private val log = LoggerFactory.getLogger(MessageCreateListener::class.java)
    }
}
