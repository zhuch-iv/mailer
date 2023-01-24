package net.zhu4.mailer.application.`in`

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandInteraction
import net.zhu4.mailer.application.out.EsiPort
import net.zhu4.mailer.application.out.SendMailRequest
import net.zhu4.mailer.application.out.TemplatePersistencePort
import net.zhu4.mailer.application.out.UserPersistencePort
import net.zhu4.mailer.domain.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import java.util.*
import java.util.logging.Level

@Service
class HandleMailEventUseCaseImpl(
    private val esiPort: EsiPort,
    private val templatePersistencePort: TemplatePersistencePort,
    private val userPersistencePort: UserPersistencePort,
    private val useCase: GetCharactersFromAttachmentsUseCase,
    private val formRecipientsList: FormRecipientsListUseCase,
    private val getAccessToken: GetAccessTokenByDiscordIdUseCase
): HandleMailEventUseCase {

    override fun mail(event: ChatInputInteractionEvent): Mono<Void> {
        return Mono.zip(
            event.interaction.user.id.asLong().getBotUser(),
            event.interaction.commandInteraction.formRecipientsList()
        )
            .createAndSendMail()
            .replyToUser(event)
            .log(HandleMailEventUseCaseImpl::class.qualifiedName, Level.FINEST)
    }

    private fun Mono<Tuple2<User, List<Recipient>>>.createAndSendMail(): Mono<List<Int>> {
        return Mono.zip(
            templatePersistencePort.findByName(templateName),
            templatePersistencePort.findByName(subjectTemplateName),
            this
        ).flatMapMany { tuple ->
            val user = tuple.t3.t1
            Flux.fromIterable(tuple.t3.t2.chunked(maxRecipients))
                .zipWith(getAccessToken.getAccessToken(user.discordId))
                .flatMap {
                    esiPort.sendMail(SendMailRequest(
                        from = user.character!!.id,
                        token = it.t2,
                        mail = createMail(tuple.t2.template, tuple.t1.template, it.t1)
                    ))
                }
        }
            .collectList()
    }

    private fun Optional<ApplicationCommandInteraction>.formRecipientsList(): Mono<List<Recipient>> {
        return formRecipientsList.formRecipientsList(
            this.flatMap { it.resolved }
                .map { useCase.getCharacters(it.attachments.values) }
                .orElse(Mono.empty())
        )
    }

    private fun Mono<List<Int>>.replyToUser(event: ChatInputInteractionEvent): Mono<Void> {
        return this
            .switchIfEmpty(Mono.just(emptyList()))
            .flatMap {
                if (it.isNotEmpty()) {
                    event.editReply("Successfully sent to ${it.size} characters")
                } else {
                    event.editReply("Nothing to do")
                }
            }
            .then()
    }

    private fun Long.getBotUser(): Mono<User> {
        return userPersistencePort.findByDiscordId(this)
    }

    private fun createMail(subject: String, template: String, recipients: List<Recipient>): Mail {
        return Mail(
            body = template,
            subject = subject,
            recipients = recipients
        )
    }

    companion object {
        private const val maxRecipients = 50
        private const val templateName = "mail"
        private const val subjectTemplateName = "subject"
    }
}
