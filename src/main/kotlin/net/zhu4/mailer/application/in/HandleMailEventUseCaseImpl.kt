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

    private fun Mono<Tuple2<User, List<List<Recipient>>>>.createAndSendMail(): Mono<List<Int>> {
        return this.getAccessToken()
            .buildRequest()
            .flatMapMany { Flux.fromIterable(it) }
            .flatMap { esiPort.sendMail(it) }
            .collectList()
    }

    private fun Mono<Tuple2<User, List<List<Recipient>>>>.getAccessToken(): Mono<List<SendMailRequest.Builder>> {
        return this.flatMap { tuple ->
            getAccessToken.getAccessToken(tuple.t1.discordId)
                .map { token ->
                    tuple.t2.map {
                        SendMailRequest.Builder()
                            .token(token)
                            .recipients(it)
                            .from(tuple.t1.character!!.id)
                    }
                }
        }
    }

    private fun Mono<List<SendMailRequest.Builder>>.buildRequest(): Mono<List<SendMailRequest>> {
        return this.flatMap {
            Mono.zip(
                templatePersistencePort.findByName(templateName),
                templatePersistencePort.findByName(subjectTemplateName),
            ).map { templates ->
                it.map {
                    it.body(templates.t1.template)
                        .subject(templates.t2.template)
                        .build()
                }
            }
        }
    }

    private fun Optional<ApplicationCommandInteraction>.formRecipientsList(): Mono<List<List<Recipient>>> {
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

    companion object {
        private const val templateName = "mail"
        private const val subjectTemplateName = "subject"
    }
}
