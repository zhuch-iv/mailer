package net.zhu4.mailer.application.out

import net.zhu4.mailer.domain.User
import org.bson.types.ObjectId
import reactor.core.publisher.Mono

interface UserPersistencePort {

    fun findByDiscordId(discordId: Long): Mono<User>

    fun findById(objectId: ObjectId): Mono<User>

    fun save(user: User): Mono<User>

    fun findByInteractionId(objectId: ObjectId): Mono<User>
}
