package net.zhu4.mailer.adapter.discord

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import reactor.core.publisher.Mono

interface DiscordCommandHandler {

    fun getCommandName(): String

    fun handle(event: ChatInputInteractionEvent): Mono<Void>
}
