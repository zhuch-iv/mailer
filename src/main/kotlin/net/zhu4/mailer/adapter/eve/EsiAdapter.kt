package net.zhu4.mailer.adapter.eve

import net.zhu4.mailer.adapter.eve.Mail.Companion.fromDomain
import net.zhu4.mailer.application.EsiClientException
import net.zhu4.mailer.application.out.EsiPort
import net.zhu4.mailer.application.EsiServerException
import net.zhu4.mailer.application.out.SendMailRequest
import net.zhu4.mailer.domain.Character
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class EsiAdapter(
    @Qualifier("esiWebClient") private val webClient: WebClient
) : EsiPort {

    override fun getCharacterIdsByNames(names: List<String>): Mono<List<Int>> {
        return webClient.post()
            .uri("/universe/ids/")
            .bodyValue(names)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, this::wrapClientError)
            .onStatus(HttpStatusCode::is5xxServerError, this::wrapServerError)
            .bodyToMono(Characters::class.java)
            .map {
                log.debug("Get from esi: $it")
                it.characters?.map { ch -> ch.id } ?: emptyList()
            }
    }

    override fun getCharacter(id: Int): Mono<Character> {
        return webClient.get()
            .uri("/characters/$id/")
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, this::wrapClientError)
            .onStatus(HttpStatusCode::is5xxServerError, this::wrapServerError)
            .bodyToMono(net.zhu4.mailer.adapter.eve.Character::class.java)
            .map {
                log.debug("Get from esi: $it")
                it.toDomain(id)
            }
    }

    override fun sendMail(request: SendMailRequest): Mono<Int> {
        return webClient.post()
            .uri("/characters/${request.from}/mail/")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${request.token}")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request.mail.fromDomain())
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, this::wrapClientError)
            .onStatus(HttpStatusCode::is5xxServerError, this::wrapServerError)
            .bodyToMono(Int::class.java)
            .doOnNext { log.debug("Get from esi: $it") }
    }

    private fun wrapServerError(response: ClientResponse): Mono<Throwable> {
        return response.bodyToMono(String::class.java).flatMap {
            log.error("${response.statusCode()}: $it")
            Mono.error(EsiServerException("${response.statusCode()}: $it"))
        }
    }

    private fun wrapClientError(response: ClientResponse): Mono<Throwable> {
        return response.bodyToMono(String::class.java).flatMap {
            if (response.statusCode().value() == 404) {
                Mono.empty()
            } else {
                log.error("${response.statusCode()}: $it")
                Mono.error(EsiClientException("${response.statusCode()}: $it"))
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(EsiAdapter::class.java)
    }
}
