package net.zhu4.mailer.application.`in`

import discord4j.core.`object`.entity.Message
import net.zhu4.mailer.domain.Character
import net.zhu4.mailer.domain.MailList
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
