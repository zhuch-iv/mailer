package net.zhu4.mailer.application.`in`

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class HandleMailEventUseCaseImpl: HandleMailEventUseCase {

    override fun mail(event: ChatInputInteractionEvent): Mono<Void> {
        return event.editReply("Not yet implemented")
            .then()
    }
}
