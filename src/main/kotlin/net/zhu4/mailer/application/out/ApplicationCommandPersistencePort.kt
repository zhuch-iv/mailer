package net.zhu4.mailer.application.out

import net.zhu4.mailer.domain.ApplicationCommand
import reactor.core.publisher.Flux

interface ApplicationCommandPersistencePort {

    fun save(commands: Flux<ApplicationCommand>): Flux<ApplicationCommand>

    fun findNotRegistered(): Flux<ApplicationCommand>
}
