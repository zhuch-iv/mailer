package net.zhu4.mailer.application.`in`

import discord4j.core.`object`.entity.Attachment
import discord4j.core.`object`.entity.Message
import discord4j.core.spec.MessageCreateSpec
import net.zhu4.mailer.application.out.UserPersistencePort
import net.zhu4.mailer.domain.Character
import net.zhu4.mailer.domain.MailList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ProcessDiscordMessageUseCaseImpl(
    private val textFileMessageUseCase: GetCharactersFromTextFileMessageUseCase,
    private val formMailListsUseCase: FormMailListsUseCase,
    private val userPersistencePort: UserPersistencePort
) : ProcessDiscordMessageUseCase {

    override fun processMessage(message: Message): Mono<Void> {
        if (hasTextAttachment(message.attachments) && message.author.isPresent) {
            return userPersistencePort.findByDiscordId(message.author.get().id.asLong())
                .filter { it.allowed }
                .flatMap {
                    textFileMessageUseCase.getCharacters(message)
                        .formMailLists()
                        .replyToUser(message)
                }
                .onErrorResume {
                    log.error("An error occurred while processing the message", it)
                    message.reply(it.message)
                }
        }
        return Mono.empty()
    }

    private fun hasTextAttachment(attachments: List<Attachment>): Boolean {
        return attachments.size == 1 &&
                attachments[0].filename == expectedFileName &&
                attachments[0].contentType.isPresent &&
                attachments[0].contentType.get() == expectedContentType
    }

    private fun Mono<List<Character>>.formMailLists(): Mono<List<MailList>> {
        return formMailListsUseCase.formMailLists(this)
    }

    private fun Mono<List<MailList>>.replyToUser(message: Message): Mono<Void> {
        return this.flatMap {
            if (it.isNotEmpty()) {
                Flux.fromIterable(it)
                    .flatMap { mail ->
                        if (mail.characterNames.length >= 2000) {
                            message.replyAsFile(outputFileName, mail.characterNames)
                        } else {
                            message.reply("```${mail.characterNames}```")
                        }
                    }
                    .then()
            } else {
                message.reply("No new eligible characters")
            }
        }
    }

    private fun Message.reply(msg: String?): Mono<Void> {
        if (msg == null) {
            return Mono.empty()
        }
        MessageCreateSpec.builder()
            .addFile("mail_list.txt", msg.byteInputStream())
            .build()
        return this.channel
            .flatMap { it.createMessage(msg) }
            .then()
    }

    private fun Message.replyAsFile(fileName: String, msg: String?): Mono<Void> {
        if (msg == null) {
            return Mono.empty()
        }
        return this.channel
            .flatMap { it.createMessage(
                MessageCreateSpec.builder()
                    .addFile(fileName, msg.byteInputStream())
                    .build()
            ) }
            .then()
    }

    companion object {
        private const val outputFileName = "mail_list.txt"
        private const val expectedFileName = "message.txt"
        private const val expectedContentType = "text/plain; charset=utf-8"
        private val log = LoggerFactory.getLogger(ProcessDiscordMessageUseCaseImpl::class.java)
    }
}
