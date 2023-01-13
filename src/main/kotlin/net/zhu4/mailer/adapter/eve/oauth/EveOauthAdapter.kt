package net.zhu4.mailer.adapter.eve.oauth

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import net.zhu4.mailer.adapter.eve.EsiAdapter
import net.zhu4.mailer.application.EveOauth2ClientException
import net.zhu4.mailer.application.EveOauth2ServerException
import net.zhu4.mailer.application.out.EveOauthPort
import net.zhu4.mailer.domain.EveAuthorization
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jwt.consumer.InvalidJwtException
import org.jose4j.jwt.consumer.JwtConsumer
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.*
import java.util.concurrent.TimeUnit


@Component
class EveOauthAdapter(
    @Qualifier("eveOauthWebClient") private val webClient: WebClient
): EveOauthPort {

    @Value("\${eve.clientId}")
    private lateinit var clientId: String

    @Value("\${eve.secretKey}")
    private lateinit var secretKey: String

    @Value("\${eve.issuer}")
    private lateinit var issuer: String

    @Value("\${eve.audience}")
    private lateinit var audience: String

    override fun createRedirectLink(state: String): String {
        return "https://login.eveonline.com/v2/oauth/authorize/" +
            "?response_type=code&redirect_uri=https%3A%2F%2Fmailer.zhu4.net%2Feve%2Fcallback" +
            "&client_id=$clientId&scope=esi-mail.send_mail.v1" +
            "&state=$state"
    }

    override fun getEveAuthorization(authorizationCode: String): Mono<EveAuthorization> {
        val formData = LinkedMultiValueMap<String, String>()
        formData.add("grant_type", "authorization_code")
        formData.add("code", authorizationCode)
        val encoded = Base64.getEncoder().encodeToString("$clientId:$secretKey".toByteArray())
        return webClient.post()
            .uri("/v2/oauth/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .header(HttpHeaders.AUTHORIZATION, "Basic $encoded")
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, this::wrapClientError)
            .onStatus(HttpStatusCode::is5xxServerError, this::wrapServerError)
            .bodyToMono(TokenResponse::class.java)
            .map { EveAuthorization(it.accessToken, it.refreshToken) }
    }

    override fun verifyAccessToken(token: String): Mono<Int> {
        return jwtConsumer()
            .map {
                try {
                    it.processToClaims(token).subject.parseSubjectId()
                } catch (e: InvalidJwtException) {
                    log.error("Invalid access token:", e)
                    0
                }
            }
            .filter { it != 0 }
    }

    override fun verifyRefreshToken(token: String): Mono<Int> {
        return jwtConsumer()
            .map {
                try {
                    it.processToClaims(token).subject.parseSubjectId()
                } catch (e: InvalidJwtException) {
                    log.error("Invalid refresh token:", e)
                    0
                }
            }
            .filter { it != 0 }
    }

    private fun String.parseSubjectId(): Int {
        return try {
            // parse "CHARACTER:EVE:123123"
            this.substring(14).toInt()
        } catch (e: NumberFormatException) {
            log.error("Invalid access token:", e)
            0
        }
    }

    private fun jwtConsumer(): Mono<JwtConsumer> {
        return getKeysFromEve()
            .map {
                JwtConsumerBuilder()
                    .setRequireExpirationTime()
                    .setAllowedClockSkewInSeconds(30)
                    .setRequireSubject()
                    .setExpectedIssuer(issuer)
                    .setExpectedAudience(audience)
                    .setVerificationKeyResolver(it.keyResolver())
                    .setJwsAlgorithmConstraints(
                        ConstraintType.PERMIT,
                        AlgorithmIdentifiers.RSA_USING_SHA256,
                        AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256
                    )
                    .build()
            }
    }

    private fun getJwksFromCache(): Mono<JsonWebKeys> {
        val keys = JWKS_CACHE.getIfPresent(JWKS_KEY_STRING)
        return if (keys == null) { Mono.empty() } else { Mono.just(keys) }
    }

    private fun getKeysFromEve(): Mono<JsonWebKeys> {
        return getJwksFromCache().switchIfEmpty {
            webClient.get()
                .uri("/oauth/jwks")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::wrapClientError)
                .onStatus(HttpStatusCode::is5xxServerError, this::wrapServerError)
                .bodyToMono(JsonWebKeys::class.java)
                .doOnNext { JWKS_CACHE.put(JWKS_KEY_STRING, it) }
        }
    }

    private fun wrapServerError(response: ClientResponse): Mono<Throwable> {
        return response.bodyToMono(String::class.java).flatMap {
            log.error("${response.statusCode()}: $it")
            Mono.error(EveOauth2ServerException("${response.statusCode()}: $it"))
        }
    }

    private fun wrapClientError(response: ClientResponse): Mono<Throwable> {
        return response.bodyToMono(String::class.java).flatMap {
            log.error("${response.statusCode()}: $it")
            Mono.error(EveOauth2ClientException("${response.statusCode()}: $it"))
        }
    }

    companion object {
        private const val JWKS_KEY_STRING = "KEY"
        private val JWKS_CACHE: Cache<String, JsonWebKeys> = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.HOURS)
            .maximumSize(1)
            .build()
        private val log = LoggerFactory.getLogger(EsiAdapter::class.java)
    }
}

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TokenResponse(
    val accessToken: String,
    val expiresIn: Int,
    val tokenType: String,
    val refreshToken: String
)
