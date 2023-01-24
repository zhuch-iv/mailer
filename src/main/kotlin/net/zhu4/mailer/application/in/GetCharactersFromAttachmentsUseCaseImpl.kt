package net.zhu4.mailer.application.`in`

import discord4j.core.`object`.entity.Attachment
import net.zhu4.mailer.application.out.CharacterPersistencePort
import net.zhu4.mailer.application.out.EsiPort
import net.zhu4.mailer.domain.Character
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@Service
class GetCharactersFromAttachmentsUseCaseImpl(
    private val characterPort: CharacterPersistencePort,
    private val webClient: WebClient,
    private val esiPort: EsiPort
) : GetCharactersFromAttachmentsUseCase {

    override fun getCharacters(attachments: Collection<Attachment>): Mono<List<Character>> {
        return attachments.getTextFiles()
            .splitNames()
            .getIdsByNames()
            .filterNonMailedCharacters()
            .getCharacters()
    }

    private fun Mono<List<Int>>.getCharacters(): Mono<List<Character>> {
        return this.flatMapMany { Flux.fromIterable(it) }
            .flatMap { esiPort.getCharacter(it) }
            .collectList()
    }

    private fun Mono<MutableList<Int>>.filterNonMailedCharacters(): Mono<List<Int>> {
        return this.flatMap {
            characterPort.findByIds(it)
                .map { list ->
                    val mailed = list.associateBy { ch -> ch.id }
                    it.filter { id -> !mailed.containsKey(id) }
                }
        }
    }

    private fun Mono<MutableList<String>>.getIdsByNames(): Mono<MutableList<Int>> {
        return this.flatMapMany { Flux.fromIterable(it.chunked(500)) }
            .flatMap { esiPort.getCharacterIdsByNames(it) }
            .flattenCollectList()
    }

    private fun Collection<Attachment>.getTextFiles(): Flux<String> {
        return if (this.isNotEmpty()) {
            this.filter { it.isTextFile() }
                .toFlux()
                .flatMap {
                    webClient.get()
                        .uri(it.url)
                        .accept(MediaType.TEXT_PLAIN)
                        .retrieve()
                        .bodyToMono(String::class.java)
                }
        } else {
            Flux.empty()
        }
    }

    private fun Attachment.isTextFile(): Boolean {
        return this.contentType.isPresent
            && this.contentType.get() == expectedContentType
    }

    private fun Flux<String>.splitNames(): Mono<MutableList<String>> {
        return this.map { it.splitNames() }
            .flattenCollectList()
    }

    private fun String.splitNames(): List<String> {
        return this.split("[\r\n]+".toRegex())
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    private fun <T> Flux<List<T>>.flattenCollectList(): Mono<MutableList<T>> {
        return this.collect({ ArrayList() }) { collector, list -> collector.addAll(list) }
    }

    companion object {
        private const val expectedContentType = "text/plain; charset=utf-8"
    }
}
