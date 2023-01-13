package net.zhu4.mailer.adapter.mongo

import net.zhu4.mailer.application.out.UserPersistencePort
import net.zhu4.mailer.domain.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.logging.Level

@Component
class UserMongoAdapter(
    private val userMongoRepository: UserMongoRepository,
    private val template: ReactiveMongoTemplate
): UserPersistencePort {

    override fun findByDiscordId(discordId: Long): Mono<User> {
        return userMongoRepository.findByDiscordId(discordId)
            .log(UserMongoAdapter::class.qualifiedName, Level.FINEST)
    }

    override fun findById(objectId: ObjectId): Mono<User> {
        return userMongoRepository.findById(objectId)
            .log(UserMongoAdapter::class.qualifiedName, Level.FINEST)
    }

    override fun save(user: User): Mono<User> {
        return userMongoRepository.save(user)
            .log(UserMongoAdapter::class.qualifiedName, Level.FINEST)
    }

    override fun findByInteractionId(objectId: ObjectId): Mono<User> {
        return template.findOne(
            Query(
                Criteria.where("interactions.${objectId.toHexString()}")
                    .exists(true)
            ),
            User::class.java
        )
    }
}
