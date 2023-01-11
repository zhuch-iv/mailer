package net.zhu4.mailer.adapter.mongo

import net.zhu4.mailer.application.out.UserPersistencePort
import net.zhu4.mailer.domain.User
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.logging.Level

@Component
class UserMongoAdapter(
    private val userMongoRepository: UserMongoRepository
): UserPersistencePort {
    override fun findByDiscordId(discordId: Long): Mono<User> {
        return userMongoRepository.findByDiscordId(discordId)
            .log(UserMongoAdapter::class.qualifiedName, Level.FINEST)
    }
}
