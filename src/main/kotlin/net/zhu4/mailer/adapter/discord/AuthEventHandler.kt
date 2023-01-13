package net.zhu4.mailer.adapter.discord

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import net.zhu4.mailer.application.`in`.HandleAuthEventUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AuthEventHandler: DiscordCommandHandler {

    @Autowired
    private lateinit var useCase: HandleAuthEventUseCase

    override fun getCommandName(): String {
        return HandleAuthEventUseCase.COMMAND_NAME
    }

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> =
        useCase.authorize(event)
}
