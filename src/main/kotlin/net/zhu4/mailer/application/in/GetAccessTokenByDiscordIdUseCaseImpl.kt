package net.zhu4.mailer.application.`in`

import net.zhu4.mailer.application.UserNotFoundException
import net.zhu4.mailer.application.out.EveOauthPort
import net.zhu4.mailer.application.out.UserPersistencePort
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.logging.Level

@Service
class GetAccessTokenByDiscordIdUseCaseImpl(
    private val eveOauthPort: EveOauthPort,
    private val userPersistencePort: UserPersistencePort
) : GetAccessTokenByDiscordIdUseCase {

    override fun getAccessToken(discordId: Long): Mono<String> {
        return userPersistencePort.findByDiscordId(discordId)
            .filter { it.authorization != null }
            .flatMap { user ->
                eveOauthPort.verifyOrRefreshAccessToken(user.authorization!!)
                    .flatMap { userPersistencePort.save(user.copy(authorization = it)) }
            }
            .switchIfEmpty(Mono.error(UserNotFoundException("User not authorized")))
            .log(GetAccessTokenByDiscordIdUseCaseImpl::class.qualifiedName, Level.FINEST)
            .mapNotNull { it.authorization?.accessToken }
    }
}
