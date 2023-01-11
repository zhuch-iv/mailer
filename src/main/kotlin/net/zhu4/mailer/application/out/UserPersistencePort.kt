package net.zhu4.mailer.application.out

import net.zhu4.mailer.domain.User
import reactor.core.publisher.Mono

interface UserPersistencePort {

    fun findByDiscordId(discordId: Long): Mono<User>
}
