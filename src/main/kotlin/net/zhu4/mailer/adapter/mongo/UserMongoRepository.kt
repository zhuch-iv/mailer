package net.zhu4.mailer.adapter.mongo

import net.zhu4.mailer.domain.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface UserMongoRepository : ReactiveMongoRepository<User, Long> {

    fun findByDiscordId(discordId: Long): Mono<User>
}
