package net.zhu4.mailer.application.out

import net.zhu4.mailer.domain.Character
import reactor.core.publisher.Mono

interface EsiPort {

    fun getCharacterIdsByNames(names: List<String>): Mono<List<Int>>

    fun getCharacter(id: Int): Mono<Character>

    fun sendMail(request: SendMailRequest): Mono<Int>
}

data class SendMailRequest(
    val mail: net.zhu4.mailer.domain.Mail,
    val from: Int,
    val token: String
)
