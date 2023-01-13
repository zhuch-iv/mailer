package net.zhu4.mailer.application.`in`

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.entity.Message
import net.zhu4.mailer.domain.ApplicationCommand
import net.zhu4.mailer.domain.Character
import net.zhu4.mailer.domain.MailList
import org.bson.types.ObjectId
import reactor.core.publisher.Mono

interface ProcessDiscordMessageUseCase {

    fun processMessage(message: Message): Mono<Void>
}

interface GetCharactersFromTextFileMessageUseCase {

    fun getCharacters(message: Message): Mono<List<Character>>
}

interface FormMailListsUseCase {

    fun formMailLists(characters: Mono<List<Character>>): Mono<List<MailList>>
}

interface AuthorizeInEveUseCase {

    fun authorizeInEve(request: EveAuthorizeRequest): Mono<Void>
}

data class EveAuthorizeRequest(
    val code: String,
    val id: ObjectId
)

interface UploadApplicationCommandsUseCase {

    fun uploadApplicationCommands(): Mono<List<ApplicationCommand>>
}

interface HandleGreetingEventUseCase {

    fun greeting(event: ChatInputInteractionEvent): Mono<Void>

    companion object {
        const val COMMAND_NAME = "greeting"
    }
}

interface HandleMailEventUseCase {

    fun mail(event: ChatInputInteractionEvent): Mono<Void>

    companion object {
        const val COMMAND_NAME = "mail"
    }
}

interface HandleAuthEventUseCase {

    fun authorize(event: ChatInputInteractionEvent): Mono<Void>

    companion object {
        const val COMMAND_NAME = "authorize"
    }
}
