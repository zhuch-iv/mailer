package net.zhu4.mailer.application.out

import net.zhu4.mailer.domain.Character
import net.zhu4.mailer.domain.Mail
import net.zhu4.mailer.domain.Recipient
import reactor.core.publisher.Mono

interface EsiPort {

    fun getCharacterIdsByNames(names: List<String>): Mono<List<Int>>

    fun getCharacter(id: Int): Mono<Character>

    fun sendMail(request: SendMailRequest): Mono<Int>
}

data class SendMailRequest(
    val mail: Mail,
    val from: Int,
    val token: String
) {
    data class Builder(
        var from: Int? = null,
        var token: String? = null,
        var body: String? = null,
        var recipients: List<Recipient>? = null,
        var subject: String? = null
    ) {

        fun subject(subject: String) = apply { this.subject = subject }
        fun body(body: String) = apply { this.body = body }
        fun from(from: Int) = apply { this.from = from }
        fun token(token: String) = apply { this.token = token }

        fun recipients(recipients: List<Recipient>) = apply { this.recipients = recipients }

        fun build() = SendMailRequest(
            Mail(body = body!!, recipients = recipients!!, subject = subject!!),
            from!!,
            token!!
        )
    }
}
