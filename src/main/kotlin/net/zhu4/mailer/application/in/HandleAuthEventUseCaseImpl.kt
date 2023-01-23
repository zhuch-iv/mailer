package net.zhu4.mailer.application.`in`

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import net.zhu4.mailer.application.UserNotFoundException
import net.zhu4.mailer.application.out.EveOauthPort
import net.zhu4.mailer.application.out.UserPersistencePort
import net.zhu4.mailer.domain.User
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class HandleAuthEventUseCaseImpl(
    private val eveOauthPort: EveOauthPort,
    private val userPersistencePort: UserPersistencePort
) : HandleAuthEventUseCase {

    override fun authorize(event: ChatInputInteractionEvent): Mono<Void> {
        return userPersistencePort.findByDiscordId(event.interaction.user.id.asLong())
            .switchIfEmpty(Mono.error(UserNotFoundException("Sorry, this command is not available for you.")))
            .createInteraction(event)
            .then()
    }

    private fun Mono<User>.createInteraction(event: ChatInputInteractionEvent): Mono<Void> {
        return this.flatMap { user ->
            val interaction = user.createInteraction()
            event.editReply(renderReply(interaction.getKey()))
                .flatMap { event.reply }
                .flatMap {
                    userPersistencePort.save(
                        user.appendMessage(interaction.getKey(), it.id.asLong(), it.channelId.asLong())
                    )
                }
                .then()
        }
    }

    private fun renderReply(key: String): String {
        val link = eveOauthPort.createRedirectLink(key)
        return """
            Your authorization link: $link
            Please do not send this link to anyone.
        """.trimIndent()
    }
}
