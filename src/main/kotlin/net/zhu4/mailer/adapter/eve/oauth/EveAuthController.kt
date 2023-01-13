package net.zhu4.mailer.adapter.eve.oauth

import net.zhu4.mailer.application.`in`.AuthorizeInEveUseCase
import net.zhu4.mailer.application.`in`.EveAuthorizeRequest
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.result.view.Rendering
import reactor.core.publisher.Mono

@Controller
class EveAuthController(
    private val authorizeInEveUseCase: AuthorizeInEveUseCase
) {

    @ResponseBody
    @GetMapping(value = ["/eve/callback"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun handleEveCallback(
        @RequestParam code: String,
        @RequestParam state: String
    ): Mono<ServerResponse> {
        log.debug("Request on callback handler - code: $code, state: $state")
        return authorizeInEveUseCase.authorizeInEve(EveAuthorizeRequest(code, ObjectId(state)))
            .then(ServerResponse.ok().build())
            .onErrorResume {
                log.error("Error while handle eve callback", it)
                ServerResponse.ok().build()
            }
    }

    @GetMapping(value = ["/eve/redirect"])
    fun handleRedirect(): Mono<Rendering> {
        return Mono.just(Rendering.view("ok").build())
    }

    companion object {
        private val log = LoggerFactory.getLogger(EveAuthController::class.java)
    }
}
