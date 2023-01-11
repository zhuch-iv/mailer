package net.zhu4.mailer.application.`in`

import discord4j.core.`object`.entity.Message
import net.zhu4.mailer.application.out.CharacterPersistencePort
import net.zhu4.mailer.application.out.EsiPort
import net.zhu4.mailer.domain.Character
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class GetCharactersFromTextFileMessageUseCaseImpl(
    private val characterPort: CharacterPersistencePort,
    private val webClient: WebClient,
    private val esiPort: EsiPort
) : GetCharactersFromTextFileMessageUseCase {

    override fun getCharacters(message: Message): Mono<List<Character>> {
        return message.getTextFile()
            .flatMap { esiPort.getCharacterIdsByNames(it.splitNames()) }
            .filterNonMailedCharacters()
            .flatMapMany { Flux.fromIterable(it) }
            .flatMap { esiPort.getCharacter(it) }
            .collectList()
    }

    private fun Message.getTextFile(): Mono<String> {
        return webClient.get()
                .uri(this.attachments[0].url)
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .bodyToMono(String::class.java)
    }

    private fun String.splitNames(): List<String> {
        return this.split("[\r\n]+".toRegex())
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    private fun Mono<List<Int>>.filterNonMailedCharacters(): Mono<List<Int>> {
        return this.flatMap {
            characterPort.findByIds(it)
                .map { list ->
                    val mailed = list.associateBy { ch -> ch.id }
                    it.filter { id -> !mailed.containsKey(id) }
                }
        }
    }
}
