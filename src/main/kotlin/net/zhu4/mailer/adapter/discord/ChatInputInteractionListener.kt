package net.zhu4.mailer.adapter.discord

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ChatInputInteractionListener(
    list: List<DiscordCommandHandler>,
) : DiscordEventListener<ChatInputInteractionEvent> {

    private val commands = list.associateBy { it.getCommandName() }

    override fun getEventType(): Class<ChatInputInteractionEvent> {
        return ChatInputInteractionEvent::class.java
    }

    override fun execute(event: ChatInputInteractionEvent): Mono<Void> {
        return (commands[event.commandName]?.handle(event)
                ?: event.reply("Sorry, there is no such command."))
            .onErrorResume {
                log.error("An error occurred while processing the message", it)
                event.editReply("${it.message}")
                    .then()
            }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ChatInputInteractionListener::class.java)
    }
}
