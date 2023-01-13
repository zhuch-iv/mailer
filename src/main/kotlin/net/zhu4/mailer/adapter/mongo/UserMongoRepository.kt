package net.zhu4.mailer.adapter.mongo

import net.zhu4.mailer.domain.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface UserMongoRepository : ReactiveMongoRepository<User, ObjectId> {

    fun findByDiscordId(discordId: Long): Mono<User>
}
