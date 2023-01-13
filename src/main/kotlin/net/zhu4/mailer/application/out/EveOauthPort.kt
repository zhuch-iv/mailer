package net.zhu4.mailer.application.out

import net.zhu4.mailer.domain.EveAuthorization
import reactor.core.publisher.Mono

interface EveOauthPort {

    fun createRedirectLink(state: String): String

    fun getEveAuthorization(authorizationCode: String): Mono<EveAuthorization>

    fun verifyAccessToken(token: String): Mono<Int>

    fun verifyRefreshToken(token: String): Mono<Int>
}
