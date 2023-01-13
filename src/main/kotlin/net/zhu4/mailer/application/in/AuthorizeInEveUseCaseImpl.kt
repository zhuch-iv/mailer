package net.zhu4.mailer.application.`in`

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.spec.MessageEditSpec
import net.zhu4.mailer.application.out.EsiPort
import net.zhu4.mailer.application.out.EveOauthPort
import net.zhu4.mailer.application.out.UserPersistencePort
import net.zhu4.mailer.domain.User
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class AuthorizeInEveUseCaseImpl(
    private val esiPort: EsiPort,
    private val eveOauthPort: EveOauthPort,
    private val userPersistencePort: UserPersistencePort,
    private val gatewayDiscordClient: GatewayDiscordClient
) : AuthorizeInEveUseCase {

    override fun authorizeInEve(request: EveAuthorizeRequest): Mono<Void> {
        return userPersistencePort.findByInteractionId(request.id)
            .getAuthToken(request.code)
            .verifyAccessToken()
            .flatMap { userPersistencePort.save(it) }
            .replyToUserSuccess(request.id.toHexString())
            .replyToUserFail(request.id)
            .then()
    }

    private fun Mono<User>.getAuthToken(code: String): Mono<User> {
        return this.flatMap { user ->
            eveOauthPort.getEveAuthorization(code).map { user.copy(authorization = it) }
        }
    }

    private fun Mono<User>.verifyAccessToken(): Mono<User> {
        return this.flatMap { user ->
            eveOauthPort.verifyAccessToken(user.authorization!!.accessToken)
                .flatMap { esiPort.getCharacter(it).map { character -> user.copy(character = character) } }
        }
    }

    private fun Mono<User>.replyToUserSuccess(interactionId: String): Mono<User> {
        return this.flatMap { it.replyToUser("Authorized as ${it.character!!.name}", interactionId) }
    }

    private fun Mono<User>.replyToUserFail(interactionId: ObjectId): Mono<User> {
        return this
            .switchIfEmpty { userPersistencePort.findByInteractionId(interactionId) }
            .onErrorResume {
                log.error("An error occurred while processing the message", it)
                userPersistencePort.findById(interactionId)
            }
            .flatMap { it.replyToUser("Something goes wrong.", interactionId.toHexString()) }
    }

    private fun User.replyToUser(message: String, interactionId: String): Mono<User> {
        val interaction = this.interactions[interactionId]
        return gatewayDiscordClient.getMessageById(
            Snowflake.of(interaction!!.messageId!!),
            Snowflake.of(interaction.channelId!!)
        )
            .flatMap { it.edit(MessageEditSpec.builder().contentOrNull(message).build()) }
            .map { _ -> this }
    }

    companion object {
        private val log = LoggerFactory.getLogger(AuthorizeInEveUseCaseImpl::class.java)
    }
}