package net.zhu4.mailer.adapter.eve.oauth

import net.zhu4.mailer.application.`in`.AuthorizeInEveUseCase
import net.zhu4.mailer.application.`in`.EveAuthorizeRequest
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.reactive.result.view.Rendering
import reactor.core.publisher.Mono

@Controller
class EveAuthController(
    private val authorizeInEveUseCase: AuthorizeInEveUseCase
) {

    @GetMapping(value = ["/eve/callback"])
    fun handleEveCallback(
        @RequestParam code: String,
        @RequestParam state: String
    ): Mono<Rendering> {
        log.debug("Request on callback handler - code: $code, state: $state")
        return authorizeInEveUseCase.authorizeInEve(EveAuthorizeRequest(code, ObjectId(state)))
            .then(rendering("OK!"))
            .onErrorResume {
                log.error("Error while handle eve callback", it)
                rendering("NOT OK!")
            }
    }

    private fun rendering(message: String): Mono<Rendering> {
        return Mono.just(
            Rendering.view("message")
                .model(mapOf("message" to message))
                .build()
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(EveAuthController::class.java)
    }
}
